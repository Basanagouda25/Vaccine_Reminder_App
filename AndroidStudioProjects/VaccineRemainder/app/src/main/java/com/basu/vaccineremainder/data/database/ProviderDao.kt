package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basu.vaccineremainder.data.model.Provider

@Dao
interface ProviderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: Provider)

    @Query("SELECT * FROM provider WHERE email = :email LIMIT 1")
    suspend fun getProviderByEmail(email: String): Provider?
}
