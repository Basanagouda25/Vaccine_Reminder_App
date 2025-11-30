package com.basu.vaccineremainder.features.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.basu.vaccineremainder.data.database.AppDatabaseProvider
import java.time.LocalDate

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            val context = applicationContext
            val db = AppDatabaseProvider.getDatabase(context)

            val today = LocalDate.now()

            val allSchedules = db.scheduleDao().getAllSchedules()

            allSchedules.forEach { schedule ->
                val dueDate = LocalDate.parse(schedule.dueDate)

                if (dueDate.isEqual(today) && schedule.status.equals("Pending", true)) {
                    val vaccine = db.vaccineDao().getVaccineById(schedule.vaccineId)
                    val child = db.childDao().getChildById(schedule.childId.toLong())

                    NotificationHelper.showNotification(
                        context,
                        title = "Vaccine Due Today!",
                        message = "${child?.name}'s ${vaccine?.vaccineName} vaccine is due today."
                    )
                }
            }

            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
