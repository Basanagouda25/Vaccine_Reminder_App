package com.basu.vaccineremainder.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
//import androidx.work.await
import com.basu.vaccineremainder.data.database.UserDao
import com.basu.vaccineremainder.data.database.ChildDao
import com.basu.vaccineremainder.data.database.NotificationDao
import com.basu.vaccineremainder.data.database.VaccineDao
import com.basu.vaccineremainder.data.database.ScheduleDao
import com.basu.vaccineremainder.data.database.ProviderDao
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.model.Provider
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.model.Schedule
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.basu.vaccineremainder.features.reports.ChildReport
import com.basu.vaccineremainder.features.reports.VaccineEntry
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import com.google.firebase.functions.ktx.functions


class AppRepository(
    private val userDao: UserDao,
    private val childDao: ChildDao,
    private val vaccineDao: VaccineDao,
    private val scheduleDao: ScheduleDao,
    private val notificationDao: NotificationDao,
    private val providerDao: ProviderDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun observeNotificationsForParent(parentEmail: String): Flow<List<AppNotification>> = callbackFlow {
        if (parentEmail.isBlank()) {
            println("❌ observeNotificationsForParent: parentEmail is blank")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        println("✅ REPO: starting notif listener for parentEmail=$parentEmail")

        val registration = Firebase.firestore
            .collectionGroup("notifications")
            .whereEqualTo("parentEmail", parentEmail)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ REPO: Error listening to notifications: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val docs = snapshot?.documents ?: emptyList()
                println("📡 REPO: SNAPSHOT CHANGE, DOC COUNT = ${docs.size}")

                docs.forEach { doc ->
                    println("📄 REPO RAW NOTIF DOC: ${doc.data}")
                }

                val list = docs.map { doc ->
                    AppNotification(
                        notificationId = 0,
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        parentId = 0 // you can map real parentId later if needed
                    )
                }

                trySend(list)
            }

        awaitClose { registration.remove() }
    }


    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }


    // ------------------ CHILD ------------------
    suspend fun insertChild(child: Child) = childDao.insertChild(child)

    fun getChildrenByParentId(parentId: Int) = childDao.getChildrenByParentId(parentId)


    suspend fun getChildById(childId: Long): Child? {
        return childDao.getChildById(childId)
    }

    suspend fun getAllVaccines(): List<Vaccine> = vaccineDao.getAllVaccines()

    suspend fun getVaccineById(vaccineId: Int) = vaccineDao.getVaccineById(vaccineId)


    suspend fun updateStatus(scheduleId: Int, newStatus: String) = scheduleDao.updateStatus(scheduleId, newStatus)

    //fun getSchedulesForChild(childId: Int): Flow<List<Schedule>>

    fun getSchedulesForChild(childId: Long): Flow<List<Schedule>> {
        return scheduleDao.getSchedulesForChild(childId)
    }


    suspend fun updateDueDate(scheduleId: Int, newDate: String) {
        scheduleDao.updateDueDate(scheduleId, newDate)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateScheduleForChild(childId: Long, dobString: String) {
        val vaccines = vaccineDao.getAllVaccines()
        val dob = LocalDate.parse(dobString) // "yyyy-MM-dd"
        val schedules = vaccines.map { vaccine ->
            val dueDate = dob.plusMonths(vaccine.recommendedAgeMonths.toLong())
            Schedule(
                childId = childId.toInt(),
                vaccineId = vaccine.vaccineId,
                dueDate = dueDate.toString(), // yyyy-MM-dd
                status = "Pending"
            )
        }
        scheduleDao.insertAllSchedules(schedules)
    }


    // ---------------- PROVIDER ----------------
    suspend fun insertProvider(provider: Provider) = providerDao.insertProvider(provider)

    suspend fun getProviderByEmail(email: String) = providerDao.getProviderByEmail(email)

    suspend fun getProviderById(providerId: String): Provider? {
        return providerDao.getProviderById(providerId)
    }

    fun getChildrenByParentEmail(parentEmail: String): Flow<List<Child>> {
        return childDao.getChildrenByParentEmail(parentEmail)
    }


    // ADD THIS FUNCTION TO AppRepository.kt


    // Add this function inside your AppRepository class
    suspend fun getAllChildrenFromFirestore(): List<Child> {
        return try {
            val snapshot = Firebase.firestore.collection("children").get().await()
            snapshot.toObjects(Child::class.java)
        } catch (e: Exception) {
            println("Error fetching all children from Firestore: ${e.message}")
            emptyList()
        }
    }
    // In AppRepository.kt
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun syncChildrenForParentFromFirestore(parentEmail: String) {
        // 1. Get all children from Firestore
        val allChildren = getAllChildrenFromFirestore()

        // 2. Filter by this parent’s email
        val myChildren = allChildren.filter { it.parentEmail == parentEmail }

        // 3. Insert into local Room DB
        childDao.insertChildren(myChildren)

        // 4. For each child, ensure schedule exists
        for (child in myChildren) {
            // Check if we already have schedule rows for this child
            val count = scheduleDao.getScheduleCountForChild(child.childId.toInt())

            if (count == 0) {
                // No schedule yet → generate it now
                try {
                    generateScheduleForChild(child.childId, child.dateOfBirth)
                } catch (e: Exception) {
                    Log.e("Repository", "Error generating schedule for childId=${child.childId}", e)
                }
            }
        }
    }

    fun observeChildrenForProvider(): Flow<List<Child>> = callbackFlow {
        val listener = Firebase.firestore
            .collection("children")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                trySend(snapshot?.toObjects(Child::class.java) ?: emptyList())
            }

        awaitClose { listener.remove() }
    }






    suspend fun saveChildToFirestore(child: Child) {
        val docRef = Firebase.firestore
            .collection("children")
            .document() // ✅ Firestore generates ID

        val childWithFirestoreId = child.copy(
            firestoreId = docRef.id,
            providerId = child.providerId
        )


        docRef.set(childWithFirestoreId).await()
    }



    // ---------------- FETCH NOTIFICATIONS FOR PARENT (FROM FIRESTORE) ----------------
    suspend fun getNotificationsForParentFromFirestore(parentId: Int): List<AppNotification> {
        return try {
            // 1: Get parent email from your User table
            val parent = userDao.getUserById(parentId) ?: return emptyList()
            val parentEmail = parent.email

            val childrenSnapshot = Firebase.firestore
                .collection("children")
                .whereEqualTo("parentEmail", parentEmail)
                .get()
                .await()

            val list = mutableListOf<AppNotification>()

            for (childDoc in childrenSnapshot.documents) {
                val childId = childDoc.id

                val notificationsSnapshot = childDoc.reference
                    .collection("notifications")
                    .orderBy("timestamp")
                    .get()
                    .await()

                for (nDoc in notificationsSnapshot.documents) {
                    val title = nDoc.getString("title") ?: "Notification"
                    val message = nDoc.getString("message") ?: ""
                    val timestamp = nDoc.getLong("timestamp") ?: System.currentTimeMillis()

                    list.add(
                        AppNotification(
                            notificationId = 0,     // Auto-generate in Room
                            title = title,
                            message = message,
                            timestamp = timestamp,
                            parentId = parentId
                        )
                    )
                }
            }

            list
        } catch (e: Exception) {
            println("Error getting notifications from Firestore: ${e.message}")
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun buildChildReport(childId: Long): ChildReport {
        val child = childDao.getChildById(childId)
            ?: throw IllegalArgumentException("No child found with ID: $childId")

        // 1️⃣ Try child-specific vaccines
        var vaccines = vaccineDao.getVaccinesForChild(childId)

        // 2️⃣ Fallback: if none found, use all vaccines (template)
        if (vaccines.isEmpty()) {
            Log.w("ReportDebug", "No vaccines for childId=$childId, falling back to getAllVaccines()")
            vaccines = vaccineDao.getAllVaccines()
        }

        val dobDate: LocalDate? = try {
            LocalDate.parse(child.dateOfBirth)
        } catch (e: Exception) {
            Log.e("ReportDebug", "Failed to parse DOB: ${child.dateOfBirth}", e)
            null
        }

        val today = LocalDate.now()
        val outFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        val storedDateFormats = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        )

        fun parseDateOrNull(raw: String?): LocalDate? {
            if (raw.isNullOrBlank()) return null
            for (fmt in storedDateFormats) {
                try {
                    return LocalDate.parse(raw, fmt)
                } catch (_: DateTimeParseException) { }
            }
            Log.w("ReportDebug", "Could not parse stored date: $raw")
            return null
        }

        val vaccineEntries = vaccines.map { v ->
            val dueDateObj: LocalDate? = when {
                !v.dueDate.isNullOrBlank() -> parseDateOrNull(v.dueDate)
                dobDate != null           -> dobDate.plusMonths(v.recommendedAgeMonths.toLong())
                else                      -> null
            }

            val displayDueDate = dueDateObj?.format(outFormatter)
            val givenDisplay = v.givenDate?.takeIf { it.isNotBlank() }

            val status = when {
                v.isCompleted -> "Completed"
                dueDateObj != null && dueDateObj.isBefore(today) -> "Missed"
                else -> "Pending"
            }

            Log.d(
                "ReportDebug",
                "vaccine=${v.vaccineName}, recAge=${v.recommendedAgeMonths}, " +
                        "dob=$dobDate, dueObj=$dueDateObj, " +
                        "given=$givenDisplay, today=$today, status=$status"
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun markVaccineCompleted(vaccineId: Int) {
        val vaccine = vaccineDao.getVaccineById(vaccineId) ?: return

        val today = LocalDate.now().toString() // "2025-12-07"

        val updated = vaccine.copy(
            isCompleted = true,
            givenDate = today
        )

        vaccineDao.insertVaccine(updated)   // REPLACE because of OnConflictStrategy.REPLACE
        Log.d("ReportDebug", "Updated vaccineId=$vaccineId as completed in Room")
    }

    suspend fun getChildrenForParent(parentEmail: String): List<Child> {
        return childDao.getChildrenForParent(parentEmail)
    }

    suspend fun callEmailFunction(
        parentId: String,
        title: String,
        message: String
    ) {
        val data = hashMapOf(
            "parentId" to parentId,
            "title" to title,
            "message" to message
        )

        Firebase.functions
            .getHttpsCallable("sendEmailNotification")
            .call(data)
            .await()
    }

}


