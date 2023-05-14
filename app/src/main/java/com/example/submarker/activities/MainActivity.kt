package com.example.submarker.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.submarker.AlarmReceiver
import com.example.submarker.R
import com.example.submarker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // alarm setup
    private lateinit var alarmManager: AlarmManager
    private val HOUR_TO_RUN = 24
    private lateinit var notif_time: String
    private var CHANNEL_ID = "Sub-Marker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        // retrieve or create unique UUID
        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        val userId = sharedPref.getString("UUID", "")
        // create UUID
        if (userId == null || userId.isEmpty()) {
            Log.d("TAG", "creating UUID")
            val deviceModelBody = hashMapOf<String, String>(
                "device_model" to android.os.Build.MODEL,
                "notif_time" to "12:00"
            )
            db.collection("UUID").add(deviceModelBody).addOnSuccessListener { documentReference ->
                with (sharedPref.edit()) {
                    putString("UUID",documentReference.id)
                    apply()
                }
            }

        }
        notif_time = "12:00"
        if (userId != null && userId != "") {
            db.collection("UUID").document(userId).get().addOnSuccessListener { documents ->
                documents.getString("notif_time")?.let { notif_time = it }
            }
            alarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager
            val alarmPendingIntent by lazy {
                val intent = Intent(this, AlarmReceiver::class.java)
                PendingIntent.getBroadcast(this, 0, intent, 0)
            }
            schedulePushNotifications(alarmPendingIntent)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.nav_bottom)
        bottomNavBar.setupWithNavController(navController)
        bottomNavBar.background = null
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddSubscriptionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun schedulePushNotifications(alarmPendingIntent: PendingIntent) {
        val calendar = GregorianCalendar.getInstance().apply {
            if (get(Calendar.HOUR_OF_DAY) >= HOUR_TO_RUN) {
                add(Calendar.DAY_OF_MONTH, 1)
            }

            set(Calendar.HOUR_OF_DAY, Integer.parseInt(notif_time.substringBefore(':')))
            set(Calendar.MINUTE, Integer.parseInt(notif_time.substringAfter(':')))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmPendingIntent
        )
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}