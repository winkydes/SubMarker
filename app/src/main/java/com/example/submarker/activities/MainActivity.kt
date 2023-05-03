package com.example.submarker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submarker.R
import com.example.submarker.adapters.SubscriptionAdapter
import com.example.submarker.data.Subscription
import com.example.submarker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        fab.setOnClickListener { view ->
            val intent = Intent(this, AddSubscriptionActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}