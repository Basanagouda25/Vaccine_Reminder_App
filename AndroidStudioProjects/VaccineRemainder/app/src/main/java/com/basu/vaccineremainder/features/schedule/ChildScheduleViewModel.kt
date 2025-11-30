package com.basu.vaccineremainder.features.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Schedule
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChildScheduleViewModel(private val repository: AppRepository) : ViewModel() {

    private val _upcoming = MutableStateFlow<List<Schedule>>(emptyList())
    val upcoming: StateFlow<List<Schedule>> = _upcoming

    private val _completed = MutableStateFlow<List<Schedule>>(emptyList())
    val completed: StateFlow<List<Schedule>> = _completed

    private val _missed = MutableStateFlow<List<Schedule>>(emptyList())
    val missed: StateFlow<List<Schedule>> = _missed

    private var currentChildId: Long = -1L

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSchedules(childId: Long) {
        currentChildId = childId
        viewModelScope.launch {
            // --- FIX: REMOVED .toInt() ---
            repository.getSchedulesForChild(childId).collect { schedules ->
                classifySchedules(schedules)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun markScheduleCompleted(scheduleId: Int) {
        viewModelScope.launch {
            repository.updateStatus(scheduleId, "Completed")
            if (currentChildId != -1L) loadSchedules(currentChildId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun markScheduleMissed(scheduleId: Int) {
        viewModelScope.launch {
            repository.updateStatus(scheduleId, "Missed")
            if (currentChildId != -1L) loadSchedules(currentChildId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun detectAndMarkMissed(childId: Long) {
        viewModelScope.launch {
            // --- FIX: REMOVED .toInt() ---
            val schedules = repository.getSchedulesForChild(childId).first()
            val today = LocalDate.now()
            schedules.filter { it.status.equals("Pending", ignoreCase = true) }.forEach { sch ->
                try {
                    val due = LocalDate.parse(sch.dueDate)
                    if (due.isBefore(today)) {
                        repository.updateStatus(sch.scheduleId, "Missed")
                    }
                } catch (e: Exception) { /* ignore */ }
            }
            // This call will now use the corrected loadSchedules function
            loadSchedules(childId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun classifySchedules(schedules: List<Schedule>) {
        val today = LocalDate.now()

        val upcomingList = ArrayList<Schedule>()
        val completedList = ArrayList<Schedule>()
        val missedList = ArrayList<Schedule>()

        for (sch in schedules) {
            when (sch.status.lowercase()) {
                "completed" -> completedList.add(sch)
                "missed" -> missedList.add(sch)
                else -> {
                    try {
                        val due = LocalDate.parse(sch.dueDate)
                        if (due.isBefore(today)) {
                            missedList.add(sch)
                        } else {
                            upcomingList.add(sch)
                        }
                    } catch (e: Exception) {
                        upcomingList.add(sch)
                    }
                }
            }
        }

        _upcoming.value = upcomingList.sortedBy { it.dueDate }
        _completed.value = completedList.sortedBy { it.dueDate }
        _missed.value = missedList.sortedBy { it.dueDate }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reschedule(scheduleId: Int, newDate: String) {
        viewModelScope.launch {
            repository.updateStatus(scheduleId, "Pending")
            repository.updateDueDate(scheduleId, newDate)
            if (currentChildId != -1L) loadSchedules(currentChildId)
        }
    }
}
