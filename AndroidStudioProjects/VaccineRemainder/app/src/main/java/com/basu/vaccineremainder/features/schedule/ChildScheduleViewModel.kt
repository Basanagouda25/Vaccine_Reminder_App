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

/**
 * ViewModel to load schedules for a child, classify them (upcoming/completed/missed),
 * mark schedules completed, and detect missed doses.
 *
 * NOTE: uses java.time.LocalDate so min API should be 26+. The project earlier used LocalDate
 * in AppRepository; this matches that approach.
 */
class ChildScheduleViewModel(private val repository: AppRepository) : ViewModel() {

    private val _allSchedules = MutableStateFlow<List<Schedule>>(emptyList())
    val allSchedules: StateFlow<List<Schedule>> = _allSchedules

    private val _upcoming = MutableStateFlow<List<Schedule>>(emptyList())
    val upcoming: StateFlow<List<Schedule>> = _upcoming

    private val _completed = MutableStateFlow<List<Schedule>>(emptyList())
    val completed: StateFlow<List<Schedule>> = _completed

    private val _missed = MutableStateFlow<List<Schedule>>(emptyList())
    val missed: StateFlow<List<Schedule>> = _missed

    private var currentChildId: Int = -1

    /**
     * Load schedules for a child and classify them into upcoming / completed / missed.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    // --- In ChildScheduleViewModel.kt ---@RequiresApi(Build.VERSION_CODES.O)
    fun loadSchedules(childId: Int) {
        currentChildId = childId
        viewModelScope.launch {
            // FIX: Use .collect to get the list from the Flow
            repository.getSchedulesForChild(childId).collect { schedules ->
                _allSchedules.value = schedules
                classifySchedules(schedules)
            }
        }
    }


    /**
     * Mark a schedule as Completed.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun markScheduleCompleted(scheduleId: Int) {
        viewModelScope.launch {
            repository.updateStatus(scheduleId, "Completed")
            // reload
            if (currentChildId != -1) loadSchedules(currentChildId)
        }
    }

    /**
     * Mark a schedule as Missed (manual override or by detection).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun markScheduleMissed(scheduleId: Int) {
        viewModelScope.launch {
            repository.updateStatus(scheduleId, "Missed")
            if (currentChildId != -1) loadSchedules(currentChildId)
        }
    }

    /**
     * Detect pending schedules whose dueDate < today and mark them as Missed.
     * This is safe to call periodically (e.g., when opening the schedule).
     */
    // --- In ChildScheduleViewModel.kt ---

    @RequiresApi(Build.VERSION_CODES.O)
    fun detectAndMarkMissed(childId: Int) {
        viewModelScope.launch {
            // FIX: Use .first() to get the current list from the Flow
            val schedules = repository.getSchedulesForChild(childId).first() // Use .first() to get a single list
            val today = LocalDate.now()
            schedules.filter { it.status.equals("Pending", ignoreCase = true) }.forEach { sch ->
                try {
                    val due = LocalDate.parse(sch.dueDate) // yyyy-MM-dd expected
                    if (due.isBefore(today)) {
                        repository.updateStatus(sch.scheduleId, "Missed")
                    }
                } catch (e: Exception) {
                    // ignore parse issues - keep status unchanged
                }
            }
            // The loadSchedules function will reload and re-classify everything correctly
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
                else -> { // Pending or any other
                    try {
                        val due = LocalDate.parse(sch.dueDate)
                        if (due.isBefore(today)) {
                            missedList.add(sch)
                        } else {
                            upcomingList.add(sch)
                        }
                    } catch (e: Exception) {
                        // if parse fails, treat as upcoming
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
            repository.updateStatus(scheduleId, "Pending")   // reset status
            repository.updateDueDate(scheduleId, newDate)    // update due date
            if (currentChildId != -1) loadSchedules(currentChildId)
        }
    }




}
