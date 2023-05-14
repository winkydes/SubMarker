package com.example.submarker.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submarker.R
import com.example.submarker.adapters.SuggestedSubscriptionAdapter
import com.example.submarker.data.SuggestedSubscription
import com.example.submarker.databinding.ActivityAddSubscriptionBinding
import com.example.submarker.dialogFragment.DatePickerFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class AddSubscriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSubscriptionBinding
    private lateinit var tvPaymentDate: TextView
    private var suggestedSubscriptionList: ArrayList<SuggestedSubscription> = ArrayList()
    private lateinit var categorySpinner: Spinner
    private lateinit var periodSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var periodAdapter: ArrayAdapter<String>
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.backableToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // textview setup
        tvPaymentDate = binding.tvPaymentDate
        val date = LocalDate.now().dayOfMonth.toString()
        tvPaymentDate.text = date

        // setup page spinner
        val categories = resources.getStringArray(R.array.categories)
        categorySpinner = binding.spinnerCategory
        val categoryAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item, categories
        )
        categorySpinner.adapter = categoryAdapter
        val period = resources.getStringArray(R.array.period)
        periodSpinner = binding.spinnerPeriod
        periodAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item, period
        )
        periodSpinner.adapter = periodAdapter
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

        // setup onClickListener
        val btnDatePicker = binding.btnDatePicker
        btnDatePicker.setOnClickListener {
            val datePicker = DatePickerFragment.newInstance(periodSpinner.selectedItem.toString())
            datePicker.show(supportFragmentManager, "datePicker")
        }

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            if (binding.etName.text.toString().isEmpty() || binding.etPrice.text.toString().isEmpty()) {
                Toast.makeText(this@AddSubscriptionActivity, "Some fields are still missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: null
            val userId = sharedPref?.getString("UUID", "") ?:""
            var paymentDate = binding.tvPaymentDate.text.toString()
            if (periodSpinner.selectedItem.toString() == "Month") {
                paymentDate = paymentDate.substringBefore('/')
            }
            val subscription = hashMapOf<String, Any>(
                "userID" to userId,
                "category" to categorySpinner.selectedItem.toString(),
                "name" to binding.etName.text.toString(),
                "periodType" to periodSpinner.selectedItem.toString(),
                "paymentDate" to paymentDate,
                "price" to binding.etPrice.text.toString()
            )
            db.collection("subscriptions").add(subscription)
            onBackPressedDispatcher.onBackPressed()
        }

        val tvSearchButton = binding.btnSearchName
        tvSearchButton.setOnClickListener {
            showPossibleNameDialog()
        }

        val defaultSub = intent.getSerializableExtra("subscription") as? SuggestedSubscription
        if (defaultSub != null) {
            changeInfo(defaultSub.name, defaultSub.price.toString().substringBefore('.'), defaultSub.period)
            categorySpinner.setSelection(categoryAdapter.getPosition(defaultSub.category))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun onDateSet(date:String) {
        tvPaymentDate.text = date
    }

    private fun showPossibleNameDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_possible_name)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        recyclerView = dialog.findViewById(R.id.rv_possible_name)
        loadNames()
        recyclerView.adapter = SuggestedSubscriptionAdapter(suggestedSubscriptionList, ::changeInfo, dialog)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tvPossibleName: TextView = dialog.findViewById(R.id.tv_possible_name)
        tvPossibleName.text = "These are popular ${categorySpinner.selectedItem.toString().lowercase(Locale.getDefault())} subscriptions"
        dialog.show()
    }

    private fun loadNames() {
        db.collection("DefaultSubscription").whereEqualTo("category", categorySpinner.selectedItem.toString()).get().addOnSuccessListener { documents ->
            try {
                suggestedSubscriptionList.clear()
                for (document in documents) {
                    val sub: SuggestedSubscription = document.toObject(SuggestedSubscription::class.java)
                    suggestedSubscriptionList.add(sub)
                }
                recyclerView.adapter?.run {
                    notifyDataSetChanged()
                }
                Log.d("TAG", recyclerView.adapter?.itemCount.toString())
            } catch (ex: Exception) {
                ex.message?.let { Log.e("TAG", it) }
            }
        }
    }

    private fun changeInfo(name: String, price: String, period: String) {
        binding.etName.setText(name)
        binding.etPrice.setText(price)
        periodSpinner.setSelection(periodAdapter.getPosition(period))
    }
}