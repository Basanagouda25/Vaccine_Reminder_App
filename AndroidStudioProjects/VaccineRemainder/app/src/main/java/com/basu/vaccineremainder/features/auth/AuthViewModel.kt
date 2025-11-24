package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow(false)
    val loginResult: StateFlow<Boolean> = _loginResult

    private val _registerResult = MutableStateFlow(false)
    val registerResult: StateFlow<Boolean> = _registerResult

    // ---------------- REGISTER USER ----------------
    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            val newUser = User(
                name = name,
                email = email,
                password = password
            )
            repository.insertUser(newUser)
            _registerResult.value = true
        }
    }

    // ---------------- LOGIN USER ----------------
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            _loginResult.value = (user?.password == password)
        }
    }
}
