package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.repository.AppRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class ProviderAuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginResult = MutableSharedFlow<Provider?>()
    val loginResult = _loginResult.asSharedFlow()

    private val _providerState = MutableStateFlow<Provider?>(null)
    val providerState: StateFlow<Provider?> = _providerState.asStateFlow()

    private val _registerSuccess = MutableSharedFlow<Boolean>()
    val registerSuccess = _registerSuccess.asSharedFlow()

    // For provider dashboard
    private val _children = MutableStateFlow<List<Child>>(emptyList())
    val children: StateFlow<List<Child>> = _children.asStateFlow()

    // -------------------------------------------------------------------------
    // LOGIN (all await() calls are INSIDE viewModelScope.launch { ... })
    // -------------------------------------------------------------------------
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            try {
                // 1) Sign in to Firebase Auth (suspend with await())
                val auth = Firebase.auth
                auth.signInWithEmailAndPassword(email, pass).await()

                // 2) Get local provider from Room on IO thread
                val localProvider = withContext(Dispatchers.IO) {
                    repository.getProviderByEmail(email)
                }

                if (localProvider != null && BCrypt.checkpw(pass, localProvider.password)) {
                    _providerState.value = localProvider
                    _loginResult.emit(localProvider)

                    // Start listening to children once provider is logged in
                    startObservingChildren()
                } else {
                    _loginResult.emit(null)
                }

            } catch (e: Exception) {
                println("Provider login failed: ${e.message}")
                _loginResult.emit(null)
            }
        }
    }

    // -------------------------------------------------------------------------
    // REGISTER (again, ALL await() are inside launch { ... })
    // -------------------------------------------------------------------------
    fun registerProvider(
        name: String,
        email: String,
        pass: String,
        clinic: String,
        phone: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val auth = Firebase.auth

                // 1) Create a Firebase Auth user (suspend with await())
                auth.createUserWithEmailAndPassword(email, pass).await()

                // 2) Hash password for local DB
                val hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt())

                // 3) Data for Firestore
                val firestoreProviderData = mapOf(
                    "name" to name,
                    "email" to email,
                    "clinicName" to clinic,
                    "phone" to phone,
                    "password" to ""    // kept only to match data class shape if needed
                )

                // 4) Save provider document to Firestore
                val documentReference = Firebase.firestore
                    .collection("providers")
                    .add(firestoreProviderData)
                    .await()

                val newProviderId = documentReference.id

                // 5) Save provider in Room DB
                val localProvider = Provider(
                    providerId = newProviderId,
                    name = name,
                    email = email,
                    password = hashedPassword,
                    clinicName = clinic,
                    phone = phone
                )

                repository.insertProvider(localProvider)

                _registerSuccess.emit(true)

            } catch (e: Exception) {
                println("Error registering provider: ${e.message}")
                _registerSuccess.emit(false)
            }
        }
    }

    fun loadProviderData() {
        println("ProviderAuthViewModel.loadProviderData(): start observing children...")
        startObservingChildren()
    }


    // -------------------------------------------------------------------------
    // CHILDREN OBSERVING FOR PROVIDER
    // -------------------------------------------------------------------------

    private var observingChildren = false

    fun startObservingChildren() {
        if (observingChildren) return
        observingChildren = true

        println("ProviderAuthViewModel: start observing children from Firestore...")

        viewModelScope.launch {
            try {
                repository.observeAllChildrenFromFirestore()
                    .collect { list ->
                        println("ProviderAuthViewModel: received ${list.size} children from Firestore")
                        _children.value = list
                    }
            } catch (e: Exception) {
                println("ProviderAuthViewModel: error observing children: ${e.message}")
            }
        }
    }

    // Optional: if you still want anonymous auth somewhere, this stays suspend
    private suspend fun ensureAnonymousAuth() {
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().await()
                println("Signed in anonymously for Firestore access")
            } catch (e: Exception) {
                println("Error signing in anonymously: ${e.message}")
            }
        }
    }

    suspend fun sendNotificationToChild(
        childDocumentId: String,
        title: String,
        message: String
    ): Boolean {
        return try {
            val notificationData = mapOf(
                "title" to title,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )

            Firebase.firestore
                .collection("children")
                .document(childDocumentId)
                .collection("notifications")
                .add(notificationData)
                .await()

            println("Successfully wrote notification for child: $childDocumentId")
            true
        } catch (e: Exception) {
            println("Error sending notification: ${e.message}")
            false
        }
    }


}

// --- FACTORY CLASS (same) ---
class ProviderAuthViewModelFactory(private val repo: AppRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProviderAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProviderAuthViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
