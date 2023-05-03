package com.example.submarker.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.submarker.R
import com.example.submarker.databinding.ActivityDataRetrievalBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DataRetrievalActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var binding: ActivityDataRetrievalBinding
    private lateinit var tvCurrentId: TextView
    private lateinit var etOldId: EditText
    private lateinit var btnConnect: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataRetrievalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Data Retrieval"

        tvCurrentId = binding.tvCurrentId

        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        val userId = sharedPref.getString("UUID", "")
        tvCurrentId.text = userId

        etOldId = binding.etOldId
        btnConnect = binding.btnConnect
        btnConnect.setOnClickListener{ view ->
            if (etOldId.text.toString().isEmpty()) {
                Toast.makeText(this@DataRetrievalActivity, "Old ID cannot be blank!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (etOldId.text.toString().equals(userId)) {
                Toast.makeText(this@DataRetrievalActivity, "Old ID cannot be same as current ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.collection("UUID").document(etOldId.text.toString()).get().addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document?.data == null) {
                        AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("No ID found")
                            .setPositiveButton("OK", null)
                            .show()
                        return@addOnCompleteListener
                    }
                    Log.d("TAG", "Cached document data: ${document.data}")
                    with (sharedPref.edit()) {
                        putString("UUID", etOldId.text.toString())
                        apply()
                    }
                    AlertDialog.Builder(this)
                        .setTitle("Success!")
                        .setMessage("All your data has been recovered!")
                        .setPositiveButton("OK"
                        ) { _, _ -> finish() }
                        .show()
                } else  {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Task Failed")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}