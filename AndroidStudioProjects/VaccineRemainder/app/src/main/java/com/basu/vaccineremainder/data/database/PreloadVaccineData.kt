package com.basu.vaccineremainder.data.database

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.basu.vaccineremainder.data.model.Vaccine

object PreloadVaccineData {

    private val initialVaccines = listOf(
        Vaccine(
            vaccineName = "BCG",
            recommendedAgeText = "At birth",
            recommendedAgeMonths = 0,
            description = "Protects against tuberculosis and severe childhood TB.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "OPV-0",
            recommendedAgeText = "At birth",
            recommendedAgeMonths = 0,
            description = "Oral polio vaccine initial dose.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "Hepatitis B-1",
            recommendedAgeText = "At birth",
            recommendedAgeMonths = 0,
            description = "Protects against Hepatitis B.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "DTwP / DTaP-1",
            recommendedAgeText = "6 weeks",
            recommendedAgeMonths = 1,
            description = "Protects against diphtheria, pertussis, and tetanus.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "OPV-1",
            recommendedAgeText = "6 weeks",
            recommendedAgeMonths = 1,
            description = "Polio vaccination dose 1.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "IPV-1",
            recommendedAgeText = "6 weeks",
            recommendedAgeMonths = 1,
            description = "Inactivated polio vaccine.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "PCV-1",
            recommendedAgeText = "6 weeks",
            recommendedAgeMonths = 1,
            description = "Prevents pneumonia & meningitis.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "RV-1",
            recommendedAgeText = "6 weeks",
            recommendedAgeMonths = 1,
            description = "Rotavirus vaccine.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "DTwP / DTaP-2",
            recommendedAgeText = "10 weeks",
            recommendedAgeMonths = 2,
            description = "Second dose for DPT.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "OPV-2",
            recommendedAgeText = "10 weeks",
            recommendedAgeMonths = 2,
            description = "Polio vaccination dose 2.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "Hib-2",
            recommendedAgeText = "10 weeks",
            recommendedAgeMonths = 2,
            description = "Prevents Haemophilus influenzae type b.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "RV-2",
            recommendedAgeText = "10 weeks",
            recommendedAgeMonths = 2,
            description = "Second dose of Rotavirus.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "MMR-1",
            recommendedAgeText = "9 months",
            recommendedAgeMonths = 9,
            description = "Protects against measles, mumps & rubella.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "Typhoid",
            recommendedAgeText = "9 months",
            recommendedAgeMonths = 9,
            description = "Protects against typhoid fever.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        ),
        Vaccine(
            vaccineName = "MMR-2",
            recommendedAgeText = "15 months",
            recommendedAgeMonths = 15,
            description = "Booster dose of MMR.",
            givenDate = null,
            dueDate = null,
            isCompleted = false
        )
    )


    fun insertInitialDataIfNeeded(context: Context, database: AppDatabase) {
        val sharedPref = context.getSharedPreferences("vaccine_pref", Context.MODE_PRIVATE)
        val alreadyInserted = sharedPref.getBoolean("vaccines_inserted", false)

        if (!alreadyInserted) {
            CoroutineScope(Dispatchers.IO).launch {
                database.vaccineDao().insertAllVaccines(initialVaccines)

                sharedPref.edit().putBoolean("vaccines_inserted", true).apply()
            }
        }
    }
}
