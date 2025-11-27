package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// In ProviderAuthViewModel.kt

class ProviderAuthViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess // Explicitly use StateFlow

    fun registerProvider(name: String, email: String, pass: String, clinic: String, phone: String) {
        viewModelScope.launch {
            val provider = Provider(
                name = name,
                email = email,
                password = pass,
                clinicName = clinic,
                phone = phone
            )
            repository.insertProvider(provider)
            _registerSuccess.value = true
        }
    }

    // --- FIX: ADD THIS FUNCTION ---
    fun onRegistrationComplete() {
        _registerSuccess.value = false
    }
    // ----------------------------

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val provider = repository.getProviderByEmail(email)
            _loginSuccess.value = provider?.password == pass
        }
    }
}




class ProviderAuthViewModelFactory(private val repo: AppRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProviderAuthViewModel(repo) as T
    }
}

