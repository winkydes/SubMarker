package com.example.submarker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.submarker.databinding.ActivityAddSubscriptionBinding

class AddSubscriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSubscriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.backableToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // setup page spinner
        val categories = resources.getStringArray(R.array.categories)
        val categorySpinner = findViewById<Spinner>(R.id.spinner_category)
        if (categorySpinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, categories
            )
            categorySpinner.adapter = adapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}