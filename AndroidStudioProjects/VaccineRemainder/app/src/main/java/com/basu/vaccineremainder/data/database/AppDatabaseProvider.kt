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

            val roomCallback = object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)

                    // run in background thread
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
                "vaccine_reminder_db"
            )
                .addCallback(roomCallback)
                .build()

            INSTANCE = instance
            instance
        }
    }
}
