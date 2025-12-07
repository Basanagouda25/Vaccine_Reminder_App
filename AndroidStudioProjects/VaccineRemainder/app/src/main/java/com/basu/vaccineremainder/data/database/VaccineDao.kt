package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basu.vaccineremainder.data.model.Vaccine

@Dao
interface VaccineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccine(vaccine: Vaccine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVaccines(vaccineList: List<Vaccine>)

    @Query("SELECT * FROM vaccines")
    suspend fun getAllVaccines(): List<Vaccine>

    @Query("SELECT * FROM vaccines WHERE vaccineId = :vaccineId LIMIT 1")
    suspend fun getVaccineById(vaccineId: Int): Vaccine?

    @Query("SELECT * FROM vaccines WHERE childId = :childId ORDER BY recommendedAgeMonths")
    suspend fun getVaccinesForChild(childId: Long): List<Vaccine>
}
