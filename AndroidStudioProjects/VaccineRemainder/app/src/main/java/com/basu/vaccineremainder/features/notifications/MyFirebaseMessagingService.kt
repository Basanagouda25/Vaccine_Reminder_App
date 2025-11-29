package com.basu.vaccineremainder.features.notifications

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import com.basu.vaccineremainder.data.database.AppDatabaseProvider
import com.basu.vaccineremainder.data.model.AppNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val parentId = remoteMessage.data["parentId"]?.toIntOrNull() ?: -1

        val title = remoteMessage.notification?.title ?: "New Notification"
        val message = remoteMessage.notification?.body ?: "You have a new message"

        //Show notification
        NotificationHelper.showNotification(
            applicationContext,
            title,
            message
        )

        //Save notification to local Room DB
        saveNotificationToDatabase(title, message,parentId)
    }

    private fun saveNotificationToDatabase(title: String, message: String,parentId: Int) {
        val db = AppDatabaseProvider.getDatabase(applicationContext)
        val notificationDao = db.notificationDao()

        CoroutineScope(Dispatchers.IO).launch {
            notificationDao.insertNotification(
                AppNotification(
                    title = title,
                    message = message,
                    timestamp = System.currentTimeMillis(), // Corrected line
                    parentId = parentId
                )
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New token: $token")
    }
}
