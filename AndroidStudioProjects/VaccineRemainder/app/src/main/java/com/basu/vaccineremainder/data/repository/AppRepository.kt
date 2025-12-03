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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await



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





    // ------------------ USER ------------------
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }


    // ------------------ CHILD ------------------
    suspend fun insertChild(child: Child) = childDao.insertChild(child)

    fun getChildrenByParentId(parentId: Int) = childDao.getChildrenByParentId(parentId)

    fun getAllChildren(): Flow<List<Child>> {
        return childDao.getAllChildren()
    }

    fun getChildrenForProvider(providerId: String): Flow<List<Child>> {
        return childDao.getChildrenForProvider(providerId)
    }


    suspend fun getChildById(childId: Long): Child? {
        return childDao.getChildById(childId)
    }

    // ------------------ VACCINE ------------------
    suspend fun insertVaccine(vaccine: Vaccine) = vaccineDao.insertVaccine(vaccine)

    suspend fun insertAllVaccines(vaccineList: List<Vaccine>) = vaccineDao.insertAllVaccines(vaccineList)

    suspend fun getAllVaccines(): List<Vaccine> = vaccineDao.getAllVaccines()

    suspend fun getVaccineById(vaccineId: Int) = vaccineDao.getVaccineById(vaccineId)

    // ------------------ SCHEDULE ------------------
    suspend fun insertSchedule(schedule: Schedule) = scheduleDao.insertSchedule(schedule)

    suspend fun insertAllSchedules(scheduleList: List<Schedule>) = scheduleDao.insertAllSchedules(scheduleList)


    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.updateSchedule(schedule)

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

    // ------------------ NOTIFICATIONS ------------------
    suspend fun insertNotification(notification: AppNotification) {
        notificationDao.insertNotification(notification)
    }

    suspend fun getNotificationsForParent(parentEmail: String): Flow<List<AppNotification>> =
        callbackFlow {
            val listener = Firebase.firestore
                .collection("notifications")
                .whereEqualTo("parentEmail", parentEmail)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val list = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(AppNotification::class.java)
                    } ?: emptyList()

                    trySend(list)
                }

            awaitClose { listener.remove() }
        }



    fun getAllNotifications(): Flow<List<AppNotification>> {
        return notificationDao.getAllNotifications()
    }

    // ---------------- PROVIDER ----------------
    suspend fun insertProvider(provider: Provider) = providerDao.insertProvider(provider)

    suspend fun getProviderByEmail(email: String) = providerDao.getProviderByEmail(email)

    suspend fun getProviderById(providerId: String): Provider? {
        return providerDao.getProviderById(providerId)
    }

    fun getAllProviders(): Flow<List<Provider>> {
        return providerDao.getAllProviders()
    }

    fun getChildrenByParentEmail(parentEmail: String): Flow<List<Child>> {
        return childDao.getChildrenByParentEmail(parentEmail)
    }

    fun getSchedulesForParent(parentEmail: String): Flow<List<Schedule>> {
        return scheduleDao.getSchedulesForParent(parentEmail)
    }


// In AppRepository.kt, add this new function

    suspend fun getProviderForCurrentUser(): Provider? {
        // Get the currently logged-in user from Firebase Auth
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            Log.d("Firestore", "No user logged in, cannot fetch provider.")
            return null // If no one is logged in, return null
        }

        // The UID of the currently logged-in user
        val uid = currentUser.uid
        Log.d("Firestore", "Fetching provider for UID: $uid")

        try {
            // Query the 'providers' collection...
            val snapshot = Firebase.firestore.collection("providers")
                .whereEqualTo("uid", uid) // ...for documents WHERE the 'uid' field matches the current user's UID
                .limit(1) // We only expect one result
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.d("Firestore", "No provider document found for UID: $uid")
                return null
            }

            // Convert the first document found into a Provider object and return it
            val provider = snapshot.documents.first().toObject(Provider::class.java)
            Log.d("Firestore", "Provider found: ${provider?.name}")
            return provider

        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching provider for current user", e)
            return null // Return null in case of an error
        }
    }


    // ADD THIS FUNCTION TO AppRepository.kt

    suspend fun getChildrenForCurrentProvider(providerId: String): List<Child> {
        if (providerId.isBlank()) {
            println("Error: Provider ID is blank, cannot fetch children.")
            return emptyList()
        }

        val childrenList = mutableListOf<Child>()
        try {
            println("Fetching children for provider ID: $providerId")

            val snapshot = Firebase.firestore.collection("children")
                // This is the crucial query:
                .whereEqualTo("providerId", providerId)
                .get()
                .await()

            childrenList.addAll(snapshot.toObjects(Child::class.java))
            println("Found ${childrenList.size} children for this provider.")

        } catch (e: Exception) {
            println("Error getting children from Firestore: ${e.message}")
            // If you get a PERMISSION_DENIED error here, it means you need a Firestore Index.
            // Check Logcat for a URL to create the index.
        }
        return childrenList
    }


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

    fun observeAllChildrenFromFirestore(): Flow<List<Child>> = callbackFlow {
        val listener = Firebase.firestore
            .collection("children")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val children = snapshot?.documents
                    ?.mapNotNull { it.toObject(Child::class.java) }
                    ?: emptyList()

                trySend(children)
            }

        awaitClose { listener.remove() }
    }



    suspend fun saveChildToFirestore(child: Child) {
        try {

            Firebase.firestore.collection("children")
                .document(child.childId.toString())
                .set(child)
                .await()
            Log.d("Firestore", "Child with ID ${child.childId} saved to Firestore successfully.")
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving child to Firestore", e)
        }
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

}
