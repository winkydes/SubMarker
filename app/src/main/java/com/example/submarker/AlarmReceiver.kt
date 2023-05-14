package com.example.submarker

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate


class AlarmReceiver : BroadcastReceiver() {
    val db = Firebase.firestore
    private var needPushNotif: Boolean = false
    private var CHANNEL_ID = "Sub-Marker"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAG", "BroadcastReceiver message")
        val sharedPref = context.getSharedPreferences("SubMarker", Context.MODE_PRIVATE) ?: return
        val userId = sharedPref.getString("UUID", "")
        Log.d("TAG", "UserID: ${userId}")
        if (userId != null) {
            db.collection("subscriptions").whereEqualTo("userID", userId).whereEqualTo("periodType", "Month").get().addOnSuccessListener { documents ->
                for (doc in documents) {
                    if (doc.getString("paymentDate") == LocalDate.now().dayOfMonth.toString()) {
                        needPushNotif = true
                    }
                }

                if (needPushNotif) {
                    Log.d("TAG", "Push notif here")
                    pushNotification(context)
                }
            }
        }

    }

    private fun pushNotification(context: Context) {
        Log.d("TAG", "Pushing Notif")
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setContentTitle("You have Subscription payment today!")
            .setContentText("Please come and check what you have to pay today!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Please come and check what you have to pay today!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, mBuilder.build())
        }
    }

}