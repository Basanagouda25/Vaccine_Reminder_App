package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

class ProviderAuthViewModel(
    private val repository: AppRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    // -------- LOGIN / REGISTER STATE ---------

    private val _loginResult = MutableSharedFlow<Provider?>()
    val loginResult = _loginResult.asSharedFlow()

    private val _providerState = MutableStateFlow<Provider?>(null)
    val providerState: StateFlow<Provider?> = _providerState.asStateFlow()

    private val _registerSuccess = MutableSharedFlow<Boolean>()
    val registerSuccess = _registerSuccess.asSharedFlow()

    // -------- CHILDREN LIST FOR PROVIDER ---------

    // This is what ViewPatientsScreen uses
    private val _childrenList = MutableStateFlow<List<Child>>(emptyList())
    val childrenList: StateFlow<List<Child>> = _childrenList.asStateFlow()

    // Optional: also keep a generic children flow if something else uses it
    private val _children = MutableStateFlow<List<Child>>(emptyList())
    val children: StateFlow<List<Child>> = _children.asStateFlow()

    // ---------------- REGISTER PROVIDER ----------------
    fun registerProvider(
        name: String,
        email: String,
        pass: String,
        clinic: String,
        phone: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1) Create Firebase Auth user (THIS makes it work across devices)
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user
                    ?: throw IllegalStateException("Firebase user is null after registration")

                val uid = firebaseUser.uid

                // 2) Hash password for local DB (optional but you already had it)
                val hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt())

                // 3) Save provider in Firestore (one doc per provider, ID = uid)
                val firestoreProviderData = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "clinicName" to clinic,
                    "phone" to phone
                )

                Firebase.firestore
                    .collection("providers")
                    .document(uid)
                    .set(firestoreProviderData)
                    .await()

                // 4) Save provider locally in Room
                val localProvider = Provider(
                    providerId = uid,
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

    // ---------------- LOGIN PROVIDER ----------------
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            try {
                // 1) Sign in with FirebaseAuth (THIS fixes “works on emulator only”)
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user ?: throw IllegalStateException("No Firebase user")
                val uid = firebaseUser.uid

                // 2) Get local provider if exists
                var localProvider = withContext(Dispatchers.IO) {
                    repository.getProviderByEmail(email)
                }

                // 3) If not in local DB (first login on this device), get from Firestore
                if (localProvider == null) {
                    val doc = Firebase.firestore
                        .collection("providers")
                        .document(uid)
                        .get()
                        .await()

                    if (doc.exists()) {
                        val name = doc.getString("name") ?: ""
                        val clinicName = doc.getString("clinicName") ?: ""
                        val phone = doc.getString("phone") ?: ""

                        localProvider = Provider(
                            providerId = uid,
                            name = name,
                            email = email,
                            password = "",    // we don't actually need it locally now
                            clinicName = clinicName,
                            phone = phone
                        )

                        withContext(Dispatchers.IO) {
                            repository.insertProvider(localProvider!!)
                        }
                    }
                }

                if (localProvider == null) {
                    println("Provider login: no local or Firestore provider found for $email")
                    _loginResult.emit(null)
                    return@launch
                }

                _providerState.value = localProvider
                _loginResult.emit(localProvider)

                // OPTIONALLY start listening to children immediately
                startObservingChildren()

            } catch (e: Exception) {
                println("Provider login failed: ${e.message}")
                _loginResult.emit(null)
            }
        }
    }

    // ---------------- CHILDREN LOADING ----------------

    fun loadProviderData() {
        // Simple wrapper – you already call this from UI
        startObservingChildren()
    }

    fun startObservingChildren() {
        println("ProviderAuthViewModel.loadProviderData(): start observing children...")
        viewModelScope.launch {
            repository.observeAllChildrenFromFirestore()
                .collect { list ->
                    println("ProviderAuthViewModel: received ${list.size} children from Firestore")
                    _childrenList.value = list
                    _children.value = list
                }
        }
    }

    // ---------------- SEND NOTIFICATION TO CHILD ----------------

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

// --- FACTORY ---
class ProviderAuthViewModelFactory(
    private val repo: AppRepository,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProviderAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProviderAuthViewModel(repo, auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
