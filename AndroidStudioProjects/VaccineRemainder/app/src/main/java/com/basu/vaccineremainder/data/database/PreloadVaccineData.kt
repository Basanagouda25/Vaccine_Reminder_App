package com.basu.vaccineremainder.data.database

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.basu.vaccineremainder.data.model.Vaccine

object PreloadVaccineData {

    private val initialVaccines = listOf(
        Vaccine(1, "BCG", "At birth", 0, "Protects against tuberculosis and severe childhood TB."),
        Vaccine(2, "OPV-0", "At birth", 0, "Oral polio vaccine initial dose."),
        Vaccine(3, "Hepatitis B-1", "At birth", 0, "Protects against Hepatitis B."),
        Vaccine(4, "DTwP / DTaP-1", "6 weeks", 1, "Protects against diphtheria, pertussis, and tetanus."),
        Vaccine(5, "OPV-1", "6 weeks", 1, "Polio vaccination dose 1."),
        Vaccine(6, "IPV-1", "6 weeks", 1, "Inactivated polio vaccine."),
        Vaccine(7, "PCV-1", "6 weeks", 1, "Prevents pneumonia & meningitis."),
        Vaccine(8, "RV-1", "6 weeks", 1, "Rotavirus vaccine."),
        Vaccine(9, "DTwP / DTaP-2", "10 weeks", 2, "Second dose for DPT."),
        Vaccine(10, "OPV-2", "10 weeks", 2, "Polio vaccination dose 2."),
        Vaccine(11, "Hib-2", "10 weeks", 2, "Prevents Haemophilus influenzae type b."),
        Vaccine(12, "RV-2", "10 weeks", 2, "Second dose of Rotavirus."),
        Vaccine(13, "MMR-1", "9 months", 9, "Protects against measles, mumps & rubella."),
        Vaccine(14, "Typhoid", "9 months", 9, "Protects against typhoid fever."),
        Vaccine(15, "MMR-2", "15 months", 15, "Booster dose of MMR.")
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
