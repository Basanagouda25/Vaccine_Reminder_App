package com.basu.vaccineremainder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.model.Schedule
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.model.Provider
//to combine all the Dao's this AppDatabase is used
@Database(
    entities = [
        User::class,
        Child::class,
        Vaccine::class,
        Schedule::class,
        AppNotification::class,
        Provider::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun childDao(): ChildDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun notificationDao(): NotificationDao
    abstract fun providerDao(): ProviderDao

}


