package com.basu.vaccineremainder.features.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.basu.vaccineremainder.data.repository.AppRepository

class ChildScheduleViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChildScheduleViewModel(repository) as T
    }
}
