package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Add these imports for background processing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    // You likely have these flows already
    private val _loginResult = MutableSharedFlow<User?>()
    val loginResult = _loginResult.asSharedFlow()

    private val _registerResult = MutableSharedFlow<Boolean>()
    val registerResult = _registerResult.asSharedFlow()

    // --- THIS IS THE FUNCTION TO REPLACE ---
    fun loginUser(email: String, password: String) {
        // Use viewModelScope to launch a coroutine that is tied to this ViewModel's lifecycle
        viewModelScope.launch {
            // withContext(Dispatchers.IO) switches this specific block to a background thread
            // This is essential for database or network calls
            val user = withContext(Dispatchers.IO) {
                repository.getUserByEmail(email)
            }

            // After the withContext block, the code automatically resumes on the main thread
            // Now we can safely check the result and update the UI state
            if (user != null && user.password == password) {
                _loginResult.emit(user)
            } else {
                _loginResult.emit(null) // Emit null for login failure
            }
        }
    }

    // Your registerUser function should also use this pattern
    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newUser = User(name = name, email = email, password = password)
                repository.insertUser(newUser)
            }
            // After successful insertion, emit the result
            _registerResult.emit(true)
        }
    }

    // Your onLogout function if you have one
    fun onLogout() {
        // Reset any state if necessary
    }
}
