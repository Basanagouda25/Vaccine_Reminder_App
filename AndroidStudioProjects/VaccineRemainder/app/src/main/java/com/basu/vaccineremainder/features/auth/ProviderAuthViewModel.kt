package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.repository.AppRepository
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

class ProviderAuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginResult = MutableSharedFlow<Provider?>()
    val loginResult = _loginResult.asSharedFlow()

    private val _providerState = MutableStateFlow<Provider?>(null)
    val providerState: StateFlow<Provider?> = _providerState.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    private val _childrenList = MutableStateFlow<List<Child>>(emptyList())
    val childrenList: StateFlow<List<Child>> = _childrenList.asStateFlow()

    // --- SECURE LOGIN FUNCTION ---
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            try {
                // Step 1: Find the provider in Firestore to confirm they exist.
                val snapshot = Firebase.firestore.collection("providers")
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    println("Login failed: No provider found with email $email")
                    _loginResult.emit(null)
                    return@launch
                }
                // This object has the correct firestore documentId
                val firestoreProvider = snapshot.documents.first().toObject(Provider::class.java)!!

                // Step 2: Get the provider from the LOCAL Room DB to get the hashed password.
                val localProvider = withContext(Dispatchers.IO) {
                    repository.getProviderByEmail(email)
                }

                if (localProvider == null) {
                    println("Login failed: Provider exists in cloud but not locally.")
                    _loginResult.emit(null)
                    return@launch
                }

                // Step 3: Securely compare the typed password with the stored hash.
                if (BCrypt.checkpw(pass, localProvider.password)) {
                    // SUCCESS!
                    println("Login successful for provider: ${firestoreProvider.name}")
                    _providerState.value = firestoreProvider
                    _loginResult.emit(firestoreProvider)
                } else {
                    // FAIL!
                    println("Login failed: Incorrect password for email $email")
                    _loginResult.emit(null)
                }
            } catch (e: Exception) {
                println("Login failed with exception: ${e.message}")
                _loginResult.emit(null)
            }
        }
    }

    // --- SECURE REGISTRATION FUNCTION ---
    fun registerProvider(name: String, email: String, pass: String, clinic: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Hash the user's password for secure storage.
                val hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt())

                // Create a provider object for Firestore (public data, NO password).
                val firestoreProvider = Provider(name = name, email = email, clinicName = clinic, phone = phone, password = "")

                // Create a provider object for local Room DB (private data, WITH hashed password).
                val localProvider = Provider(name = name, email = email, password = hashedPassword, clinicName = clinic, phone = phone)

                // 1. Save public data to Firestore.
                Firebase.firestore.collection("providers").add(firestoreProvider).await()

                // 2. Save private data (with hashed password) to the local Room DB.
                repository.insertProvider(localProvider)

                _registerSuccess.value = true

            } catch (e: Exception) {
                println("Error registering provider: ${e.message}")
                _registerSuccess.value = false
            }
        }
    }

    // --- DATA LOADING AND OTHER FUNCTIONS ---

    fun loadProviderData() {
        println("loadProviderData called. Fetching children...")
        loadChildrenForCurrentProvider()
    }

    private fun loadChildrenForCurrentProvider() {
        viewModelScope.launch {
            val currentProvider = _providerState.value ?: return@launch
            val providerDocumentId = currentProvider.providerId
            if (providerDocumentId.isBlank()) {
                println("Cannot fetch children: Provider ID is blank.")
                return@launch
            }

            println("Fetching children for provider ID: $providerDocumentId")
            val children = withContext(Dispatchers.IO) {
                repository.getChildrenForCurrentProvider(providerDocumentId)
            }
            _childrenList.value = children
            println("Found ${children.size} children.")
        }
    }

    suspend fun sendNotificationToChild(childDocumentId: String, title: String, message: String): Boolean {
        return try {
            val notificationData = mapOf(
                "title" to title,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )
            Firebase.firestore.collection("children").document(childDocumentId)
                .collection("notifications")
                .add(notificationData)
                .await()
            println("Successfully wrote notification for child: $childDocumentId")
            true // Return true on success
        } catch (e: Exception) {
            println("Error sending notification: ${e.message}")
            false // Return false on failure
        }
    }

    fun onRegistrationComplete() {
        _registerSuccess.value = false
    }
}

// --- FACTORY CLASS (Remains the same) ---
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
