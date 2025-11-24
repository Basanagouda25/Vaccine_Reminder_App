package com.basu.vaccineremainder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.model.Schedule

//to combine all the Dao's this AppDatabase is used
@Database(
    entities = [
        User::class,
        Child::class,
        Vaccine::class,
        Schedule::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun childDao(): ChildDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun scheduleDao(): ScheduleDao
}
