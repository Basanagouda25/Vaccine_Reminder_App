package com.basu.vaccineremainder.features.childprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddChildViewModel(private val repository: AppRepository) : ViewModel() {

    /**
     * Fetches all providers from the database and exposes them as a StateFlow.
     * This list will be used to populate the dropdown menu on the AddChildScreen.
     */
    val allProviders: StateFlow<List<Provider>> = repository.getAllProviders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Inserts a new child into the database.
     */
    @RequiresApi(Build.VERSION_CODES.O) // Add this annotation
    fun addChild(child: Child) {
        viewModelScope.launch {
            repository.insertChild(child)

            // Find the child we just inserted to get its new ID
            repository.getChildrenByParentId(child.parentId).firstOrNull()?.lastOrNull()?.let { newChild ->
                // Now generate the schedule for this new child
                repository.generateScheduleForChild(newChild.childId, newChild.dateOfBirth)
            }
        }
    }
}

/**
 * Factory for creating an instance of AddChildViewModel with a repository dependency.
 */
class AddChildViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddChildViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddChildViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
