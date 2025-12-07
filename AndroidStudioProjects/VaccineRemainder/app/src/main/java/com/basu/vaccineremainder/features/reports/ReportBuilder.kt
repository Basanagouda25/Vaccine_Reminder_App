package com.basu.vaccineremainder.features.reports

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.basu.vaccineremainder.data.database.ChildDao
import com.basu.vaccineremainder.data.database.VaccineDao
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
suspend fun buildChildReport(
    childDao: ChildDao,
    vaccineDao: VaccineDao,
    childId: Long
): ChildReport {

    val child = childDao.getChildById(childId)
        ?: throw IllegalArgumentException("No child found with ID: $childId")

    // 🔁 IMPORTANT: vaccines only for THIS child
    val vaccines = vaccineDao.getVaccinesForChild(childId)

    // Parse DOB
    val dobDate: LocalDate? = try {
        LocalDate.parse(child.dateOfBirth)   // e.g. "2024-12-21"
    } catch (e: Exception) {
        Log.e("ReportDebug", "Failed to parse DOB: ${child.dateOfBirth}", e)
        null
    }

    val today = LocalDate.now()
    val outFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    // formats for stored dates (if you ever store dueDate as string)
    val storedDateFormats = listOf(
        DateTimeFormatter.ISO_LOCAL_DATE,              // 2024-12-07
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),     // 07/12/2024
        DateTimeFormatter.ofPattern("dd-MM-yyyy")      // 07-12-2024
    )

    fun parseDateOrNull(raw: String?): LocalDate? {
        if (raw.isNullOrBlank()) return null
        for (fmt in storedDateFormats) {
            try {
                return LocalDate.parse(raw, fmt)
            } catch (_: DateTimeParseException) {
                // try next
            }
        }
        Log.w("ReportDebug", "Could not parse stored date: $raw")
        return null
    }

    val vaccineEntries = vaccines.map { v ->
        // 1) Choose due date:
        //    - if v.dueDate exists, parse it
        //    - else use DOB + recommendedAgeMonths
        val dueDateObj: LocalDate? = when {
            !v.dueDate.isNullOrBlank() -> parseDateOrNull(v.dueDate)
            dobDate != null           -> dobDate.plusMonths(v.recommendedAgeMonths.toLong())
            else                      -> null
        }

        val displayDueDate: String? = dueDateObj?.format(outFormatter)

        // 2) Given date: keep as string for now
        val givenDisplay: String? = v.givenDate
            ?.takeIf { it.isNotBlank() }

        // 3) Status
        val status = when {
            v.isCompleted -> "Completed"
            dueDateObj != null && dueDateObj.isBefore(today) -> "Missed"
            else -> "Pending"
        }

        Log.d(
            "ReportDebug",
            "vaccine=${v.vaccineName}, recAge=${v.recommendedAgeMonths}, " +
                    "dob=$dobDate, dueObj=$dueDateObj, given=$givenDisplay, " +
                    "today=$today, status=$status"
        )

        VaccineEntry(
            name = v.vaccineName,
            dateGiven = givenDisplay,
            dueDate = displayDueDate,
            status = status
        )
    }

    return ChildReport(
        childName = child.name,
        parentEmail = child.parentEmail,
        dob = child.dateOfBirth,
        vaccines = vaccineEntries
    )
}
