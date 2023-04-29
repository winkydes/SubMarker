package com.example.submarker.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.submarker.R
import com.example.submarker.databinding.ActivityAddSubscriptionBinding
import com.example.submarker.dialogFragment.DatePickerFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime

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
        val date = LocalDate.now().dayOfMonth.toString()
        tvPaymentDate.text = date

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
            periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (parent?.selectedItemPosition == 0)
                        tvPaymentDate.text = LocalDate.now().dayOfMonth.toString()
                    else {
                        val sdf = SimpleDateFormat("dd/MM")
                        val dateString: String = sdf.format(System.currentTimeMillis())
                        tvPaymentDate.text = dateString
                    }
                }

            }
        }

        // setup onClickListener
        val btnDatePicker = findViewById<Button>(R.id.btn_date_picker)
        btnDatePicker.setOnClickListener {view ->
            val datePicker = DatePickerFragment.newInstance(periodSpinner.selectedItem.toString())
            datePicker.show(supportFragmentManager, "datePicker")
        }

        val btnSubmit = findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener {view ->
            if (findViewById<EditText>(R.id.et_name).text.toString().isEmpty() || findViewById<EditText>(R.id.et_price).text.toString().isEmpty()) {
                Toast.makeText(this@AddSubscriptionActivity, "Some fields are still missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: null
            val userId = sharedPref?.getString("UUID", "") ?:""
            var paymentDate = findViewById<TextView>(R.id.tv_payment_date).text.toString()
            if (periodSpinner.selectedItem.toString() == "Month") {
                paymentDate = paymentDate.substringBefore('/')
            }
            val subscription = hashMapOf<String, Any>(
                "userID" to userId,
                "category" to categorySpinner.selectedItem.toString(),
                "name" to findViewById<EditText>(R.id.et_name).text.toString(),
                "period" to findViewById<EditText>(R.id.et_period).text.toString(),
                "periodType" to periodSpinner.selectedItem.toString(),
                "paymentDate" to paymentDate,
                "price" to findViewById<EditText>(R.id.et_price).text.toString()
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