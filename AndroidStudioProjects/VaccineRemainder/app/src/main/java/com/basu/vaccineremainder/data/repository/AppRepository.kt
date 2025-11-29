package com.basu.vaccineremainder.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.basu.vaccineremainder.data.database.UserDao
import com.basu.vaccineremainder.data.database.ChildDao
import com.basu.vaccineremainder.data.database.NotificationDao
import com.basu.vaccineremainder.data.database.VaccineDao
import com.basu.vaccineremainder.data.database.ScheduleDao
import com.basu.vaccineremainder.data.database.ProviderDao
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.model.Schedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class AppRepository(
    private val userDao: UserDao,
    private val childDao: ChildDao,
    private val vaccineDao: VaccineDao,
    private val scheduleDao: ScheduleDao,
    private val notificationDao: NotificationDao,
    private val providerDao: ProviderDao,
) {

    // ------------------ USER ------------------
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }


    // ------------------ CHILD ------------------
    suspend fun insertChild(child: Child) = childDao.insertChild(child)

    fun getChildrenByParentId(parentId: Int) = childDao.getChildrenByParentId(parentId)

    fun getAllChildren(): Flow<List<Child>> {
        return childDao.getAllChildren()
    }

    // --- FIX #1: Replace the old suspend function with this Flow-based one ---
    fun getChildrenByProviderId(providerId: Int): Flow<List<Child>> {
        // This now calls the new Flow function from your ChildDao
        return childDao.getChildrenByProviderId(providerId)
    }
    // --------------------------------------------------------------------

    suspend fun getChildById(childId: Int): Child? {
        return childDao.getChildById(childId)
    }

    // ------------------ VACCINE ------------------
    suspend fun insertVaccine(vaccine: Vaccine) = vaccineDao.insertVaccine(vaccine)

    suspend fun insertAllVaccines(vaccineList: List<Vaccine>) = vaccineDao.insertAllVaccines(vaccineList)

    suspend fun getAllVaccines(): List<Vaccine> = vaccineDao.getAllVaccines()

    suspend fun getVaccineById(vaccineId: Int) = vaccineDao.getVaccineById(vaccineId)

    // ------------------ SCHEDULE ------------------
    suspend fun insertSchedule(schedule: Schedule) = scheduleDao.insertSchedule(schedule)

    suspend fun insertAllSchedules(scheduleList: List<Schedule>) = scheduleDao.insertAllSchedules(scheduleList)


    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.updateSchedule(schedule)

    suspend fun updateStatus(scheduleId: Int, newStatus: String) = scheduleDao.updateStatus(scheduleId, newStatus)

    //fun getSchedulesForChild(childId: Int): Flow<List<Schedule>>

    fun getSchedulesForChild(childId: Int): Flow<List<Schedule>> {
        return scheduleDao.getSchedulesForChild(childId)
    }


    suspend fun updateDueDate(scheduleId: Int, newDate: String) {
        scheduleDao.updateDueDate(scheduleId, newDate)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateScheduleForChild(childId: Int, dobString: String) {
        val vaccines = vaccineDao.getAllVaccines()
        val dob = LocalDate.parse(dobString) // "yyyy-MM-dd"
        val schedules = vaccines.map { vaccine ->
            val dueDate = dob.plusMonths(vaccine.recommendedAgeMonths.toLong())
            Schedule(
                childId = childId,
                vaccineId = vaccine.vaccineId,
                dueDate = dueDate.toString(), // yyyy-MM-dd
                status = "Pending"
            )
        }
        scheduleDao.insertAllSchedules(schedules)
    }

    // ------------------ NOTIFICATIONS ------------------
    suspend fun insertNotification(notification: AppNotification) {
        notificationDao.insertNotification(notification)
    }

    fun getNotificationsForParent(parentId: Int): Flow<List<AppNotification>> {
        return notificationDao.getNotificationsForParent(parentId)
    }

    fun getAllNotifications(): Flow<List<AppNotification>> {
        return notificationDao.getAllNotifications()
    }

    // ---------------- PROVIDER ----------------
    suspend fun insertProvider(provider: Provider) = providerDao.insertProvider(provider)

    suspend fun getProviderByEmail(email: String) = providerDao.getProviderByEmail(email)

    suspend fun getProviderById(providerId: Int): Provider? {
        return providerDao.getProviderById(providerId)
    }

    fun getAllProviders(): Flow<List<Provider>> {
        return providerDao.getAllProviders()
    }
}
