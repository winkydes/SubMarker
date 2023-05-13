package com.example.submarker.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.submarker.R
import com.example.submarker.databinding.ActivityBugReportBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BugReportActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private lateinit var btnSubmit: Button
    private lateinit var etBugContent: EditText
    private lateinit var binding: ActivityBugReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBugReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Report a Bug"

        btnSubmit = binding.btnSubmit
        etBugContent = binding.etBugContent
        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: null
        val userId = sharedPref?.getString("UUID", "") ?:""
        btnSubmit.setOnClickListener {
            val report = hashMapOf<String, String>(
                "userID" to userId,
                "content" to etBugContent.text.toString()
            )
            db.collection("report").add(report)
            Toast.makeText(this@BugReportActivity, "Thank you very much for your report, our team will follow up your case soon!", Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}