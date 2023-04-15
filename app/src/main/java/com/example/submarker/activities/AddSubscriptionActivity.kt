package com.example.submarker.activities

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.submarker.R
import com.example.submarker.databinding.ActivityAddSubscriptionBinding
import com.example.submarker.dialogFragment.DatePickerFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class AddSubscriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSubscriptionBinding
    private lateinit var tvPaymentDate: TextView
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.backableToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // textview setup
        tvPaymentDate = findViewById(R.id.tv_payment_date)
        val date = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd/MM")
        val dateString: String = sdf.format(date)
        tvPaymentDate.text = dateString

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
        val period = resources.getStringArray(R.array.period)
        val periodSpinner = findViewById<Spinner>(R.id.spinner_period)
        if (periodSpinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, period
            )
            periodSpinner.adapter = adapter
        }

        // setup onClickListener
        val btnDatePicker = findViewById<Button>(R.id.btn_date_picker)
        btnDatePicker.setOnClickListener {view ->
            DatePickerFragment().show(supportFragmentManager, "datePicker")
        }

        val btnSubmit = findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener {view ->
            val subscription = hashMapOf<String, Any>(
                "userID" to "underDevelopment",
                "category" to categorySpinner.selectedItem.toString(),
                "name" to findViewById<EditText>(R.id.et_name).text.toString(),
                "period" to findViewById<EditText>(R.id.et_period).text.toString(),
                "periodType" to periodSpinner.selectedItem.toString(),
                "paymentDate" to findViewById<TextView>(R.id.tv_payment_date).text.toString()
            )
            db.collection("subscriptions").add(subscription)
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun onDateSet(date:String) {
        tvPaymentDate.text = date
    }
}