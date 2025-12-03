// NotificationViewModel.kt
package com.basu.vaccineremainder.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: AppRepository,
    private val parentEmail: String
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    init {
        observeNotifications()
    }

    private fun observeNotifications() {
        if (parentEmail.isBlank()) {
            println("❌ VM: parentEmail is blank")
            _notifications.value = emptyList()
            return
        }

        println("✅ VM: start observing notifications for $parentEmail")

        viewModelScope.launch {
            repository.observeNotificationsForParent(parentEmail)
                .collectLatest { list ->
                    println("🔔 VM RECEIVED ${list.size} notifications")
                    _notifications.value = list
                }
        }
    }
}

class NotificationViewModelFactory(
    private val repository: AppRepository,
    private val parentEmail: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repository, parentEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
