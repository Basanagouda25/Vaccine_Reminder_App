package com.basu.vaccineremainder.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.basu.vaccineremainder.data.database.UserDao
import com.basu.vaccineremainder.data.database.ChildDao
import com.basu.vaccineremainder.data.database.VaccineDao
import com.basu.vaccineremainder.data.database.ScheduleDao
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.model.Schedule
import java.time.LocalDate

class AppRepository(
    private val userDao: UserDao,
    private val childDao: ChildDao,
    private val vaccineDao: VaccineDao,
    private val scheduleDao: ScheduleDao
) {

    // ------------------ USER ------------------
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    // ------------------ CHILD ------------------
    suspend fun insertChild(child: Child) = childDao.insertChild(child)

    suspend fun getChildrenByParentId(parentId: Int) = childDao.getChildrenByParentId(parentId)

    suspend fun getChildById(childId: Int) = childDao.getChildById(childId)

    // ------------------ VACCINE ------------------
    suspend fun insertVaccine(vaccine: Vaccine) = vaccineDao.insertVaccine(vaccine)

    suspend fun insertAllVaccines(vaccineList: List<Vaccine>) = vaccineDao.insertAllVaccines(vaccineList)

    suspend fun getAllVaccines(): List<Vaccine> = vaccineDao.getAllVaccines()

    suspend fun getVaccineById(vaccineId: Int) = vaccineDao.getVaccineById(vaccineId)

    // ------------------ SCHEDULE ------------------
    suspend fun insertSchedule(schedule: Schedule) = scheduleDao.insertSchedule(schedule)

    suspend fun insertAllSchedules(scheduleList: List<Schedule>) = scheduleDao.insertAllSchedules(scheduleList)

    suspend fun getSchedulesForChild(childId: Int) = scheduleDao.getSchedulesForChild(childId)

    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.updateSchedule(schedule)

    suspend fun updateStatus(scheduleId: Int, newStatus: String) = scheduleDao.updateStatus(scheduleId, newStatus)


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

}
