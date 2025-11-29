package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// --- START: Import Firebase Functions ---
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
// --- END: Import Firebase Functions ---
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProviderAuthViewModel(private val repository: AppRepository) : ViewModel() {

    // --- Add a reference to Firebase Functions ---
    private lateinit var functions: FirebaseFunctions

    init {
        // Initialize Firebase Functions
        functions = Firebase.functions
    }

    private val _loginResult = MutableSharedFlow<Provider?>()
    val loginResult = _loginResult.asSharedFlow()

    private val _providerState = MutableStateFlow<Provider?>(null) // Renamed from _provider
    val providerState: StateFlow<Provider?> = _providerState.asStateFlow() // Renamed from provider


    private val _childrenList = MutableStateFlow<List<Child>>(emptyList())
    val childrenList: StateFlow<List<Child>> = _childrenList.asStateFlow()


    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val provider = withContext(Dispatchers.IO) {
                repository.getProviderByEmail(email)
            }
            if (provider?.password == pass) {
                _loginResult.emit(provider)
            } else {
                _loginResult.emit(null)
            }
        }
    }

    fun loadProviderData(providerId: Int): Job { // Change: Add ": Job"
        return viewModelScope.launch(Dispatchers.IO) { // Change: Add "return"
            _providerState.value = repository.getProviderById(providerId)
        }
    }


    fun loadAllChildren() {
        viewModelScope.launch {
            repository.getAllChildren().collect { allChildren ->
                _childrenList.value = allChildren
            }
        }
    }



    fun registerProvider(name: String, email: String, pass: String, clinic: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val provider = Provider(
                name = name, email = email, password = pass, clinicName = clinic, phone = phone
            )
            repository.insertProvider(provider)
            _registerSuccess.value = true
        }
    }

    /**
     * Triggers a Firebase Cloud Function to send a push notification.
     * This now communicates with your backend.
     */
    suspend fun sendNotificationToChild(childId: Int, title: String, message: String): Boolean {
        return try {
            val child = repository.getChildById(childId)
            if (child == null) {
                println("Error: Child not found with ID $childId")
                return false
            }

            // 1. Save a record of the notification locally first.
            val notification = AppNotification(
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                parentId = child.parentId
            )
            repository.insertNotification(notification)

            // 2. Prepare the data to send to the Cloud Function.
            val data = hashMapOf(
                "parentId" to child.parentId,
                "title" to title,
                "message" to message
            )

            // 3. Call the cloud function named "sendPushNotification".
            // The `withContext(Dispatchers.IO)` ensures this network call is off the main thread.
            functions
                .getHttpsCallable("sendPushNotification")
                .call(data)
                .await() // Wait for the function to complete

            println("Successfully triggered Cloud Function for parentId: ${child.parentId}")
            true // Indicate success

        } catch (e: Exception) {
            // Log the specific Firebase error
            println("Error calling cloud function: ${e.message}")
            e.printStackTrace()
            false // Indicate failure
        }
    }


    fun onRegistrationComplete() {
        _registerSuccess.value = false
    }
}

// The factory class remains the same, no changes needed here.
class ProviderAuthViewModelFactory(private val repo: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProviderAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProviderAuthViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
