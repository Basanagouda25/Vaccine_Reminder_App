package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccines")
data class Vaccine(
    @PrimaryKey(autoGenerate = true)
    val vaccineId: Int = 0,
    val name: String,              // Vaccine name e.g. "BCG", "OPV", "Hepatitis B"
    val recommendedAgeWeeks: Int,  // Age in weeks when it should be taken
    val description: String        // Short details / purpose
)
