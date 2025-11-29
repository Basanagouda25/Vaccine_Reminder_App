package com.basu.vaccineremainder.features.dashboardimport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: AppRepository) : ViewModel() {

    // --- State for the currently logged-in user ---
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    // --- State for the list of children for the user ---
    private val _children = MutableStateFlow<List<Child>>(emptyList())
    val children = _children.asStateFlow()

    /**
     * Loads all necessary data for the dashboard based on the logged-in user's ID.
     */
    fun loadUserData(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch the user's profile information
            _user.value = repository.getUserByEmail(email = "email")

            // Fetch the list of children for that user
            repository.getChildrenByParentId(userId).collect { childList ->
                _children.value = childList
            }
        }
    }
}

/**
 * Factory for creating UserViewModel with a repository dependency.
 */
class UserViewModelFactory(private val repo: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
