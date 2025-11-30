package com.basu.vaccineremainder.data.database

import com.basu.vaccineremainder.data.database.Converters

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.model.Schedule
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.model.Vaccine

@Database(
    entities = [
        User::class,
        Child::class,
        Vaccine::class,
        Schedule::class,
        AppNotification::class,
        Provider::class
    ],
    version = 4,
    exportSchema = false
)
// This will now correctly reference your project's Converters class
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun notificationDao(): NotificationDao
    abstract fun childDao(): ChildDao
    abstract fun providerDao(): ProviderDao
}
