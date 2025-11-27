package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    // --- FIX 1: Change from Boolean to User? ---
    private val _loginResult = MutableStateFlow<User?>(null)
    val loginResult: StateFlow<User?> = _loginResult
    // ------------------------------------------

    private val _registerResult = MutableStateFlow(false)
    val registerResult: StateFlow<Boolean> = _registerResult

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            val newUser = User(name = name, email = email, password = password)
            repository.insertUser(newUser)
            _registerResult.value = true
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user?.password == password) {
                // --- FIX 2: Emit the successful User object ---
                _loginResult.value = user
            } else {
                _loginResult.value = null // Emit null on failure
            }
        }
    }

    fun onLogout() {
        // --- FIX 3: Reset state to null ---
        _loginResult.value = null
    }
}
