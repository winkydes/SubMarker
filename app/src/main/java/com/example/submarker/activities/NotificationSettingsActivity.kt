package com.example.submarker.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.submarker.R
import com.example.submarker.databinding.ActivityNotificationSettingsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NotificationSettingsActivity : AppCompatActivity() {
    val db = Firebase.firestore
    lateinit var binding: ActivityNotificationSettingsBinding
    lateinit var mTimePicker: TimePicker
    lateinit var mBtnSave: Button
    var hour: Int = 0
    var minute: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notification Settings"

        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        val userId = sharedPref.getString("UUID", "")

        mTimePicker = binding.timePicker
        mBtnSave = binding.btnSave

        if (userId != null) {
            db.collection("UUID").document(userId).get().addOnSuccessListener { doc ->
                if (doc != null) {
                    val timeStr: String? = doc.getString("notif_time")
                    hour = timeStr?.substringBefore(":")?.let { Integer.parseInt(it) }!!
                    minute = timeStr.substringAfter(":").let { Integer.parseInt(it) }
                    mTimePicker.hour = timeStr.substringBefore(":").let { Integer.parseInt(it) }
                    mTimePicker.minute = timeStr.substringAfter(":").let { Integer.parseInt(it) }
                }
            }
        }

        mTimePicker.setOnTimeChangedListener { _, p1, p2 ->
            hour = p1
            minute = p2
        }

        mBtnSave.setOnClickListener {
            if (userId != null) {
                db.collection("UUID").document(userId).update("notif_time", "$hour:$minute").addOnCompleteListener{result ->
                    if (result.isSuccessful) {
                        finish()
                    } else {
                        Toast.makeText(this@NotificationSettingsActivity, "Error, update is not done", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }
            }
            //TODO: update alarm schedule here
        }

        binding.clNotifPermission.setOnClickListener{
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}