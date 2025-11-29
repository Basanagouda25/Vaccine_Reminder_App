package com.basu.vaccineremainder.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppDatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            // Re-check instance in case it was initialized while the thread was waiting
            INSTANCE?.let {
                return it
            }

            val roomCallback = object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)

                    // run in background thread to preload data
                    CoroutineScope(Dispatchers.IO).launch {
                        INSTANCE?.let { database ->
                            PreloadVaccineData.insertInitialDataIfNeeded(context, database)
                        }
                    }
                }
            }

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "vaccine_reminder_db" // The database file name
            )
                .addCallback(roomCallback)
                .fallbackToDestructiveMigration()
                // ---------------------
                .build()

            INSTANCE = instance
            instance
        }
    }
}
