package com.basu.vaccineremainder.features.childprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.toInt // This import might now be unused and can be removedimport androidx.lifecycle.ViewModel
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

    val allProviders: StateFlow<List<Provider>> = repository.getAllProviders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun addChild(child: Child) {
        viewModelScope.launch {
            // This returns a Long, which is correct.
            val newChildId = repository.insertChild(child)

            // The 'child' object's ID is 0, so we create a copy with the correct ID.
            val childWithId = child.copy(childId = newChildId)

            // This now works perfectly.
            repository.saveChildToFirestore(childWithId)
            repository.generateScheduleForChild(childWithId.childId, childWithId.dateOfBirth)
        }
    }

}

class AddChildViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddChildViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddChildViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
