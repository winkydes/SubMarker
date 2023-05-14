package com.example.submarker.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.submarker.R
import com.example.submarker.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class DashboardFragment : Fragment() {
    val db = Firebase.firestore
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var pbVideoStreaming: ProgressBar
    private lateinit var pbMusicStreaming: ProgressBar
    private lateinit var pbBills: ProgressBar
    private lateinit var pbHealthcare: ProgressBar
    private lateinit var pbOthers: ProgressBar

    private lateinit var tvVideo: TextView
    private lateinit var tvMusic: TextView
    private lateinit var tvBills: TextView
    private lateinit var tvHealthcare: TextView
    private lateinit var tvOthers: TextView

    private var videoMonthPayment: Float = 0f
    private var musicMonthPayment: Float = 0f
    private var billsMonthPayment: Float = 0f
    private var healthcareMonthPayment: Float = 0f
    private var othersMonthPayment: Float = 0f

    private var videoYearPayment: Float = 0f
    private var musicYearPayment: Float = 0f
    private var billsYearPayment: Float = 0f
    private var healthcareYearPayment: Float = 0f
    private var othersYearPayment: Float = 0f

    private lateinit var tvYourExpenses: TextView
    private lateinit var tvTotal: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pbVideoStreaming = binding.pbVideoStreaming
        pbMusicStreaming = binding.pbMusicStreaming
        pbBills = binding.pbBills
        pbHealthcare = binding.pbHealthcare
        pbOthers = binding.pbOthers

        tvVideo = binding.tvVideo
        tvMusic = binding.tvMusic
        tvBills = binding.tvBills
        tvHealthcare = binding.tvHealthcare
        tvOthers = binding.tvOthers

        tvYourExpenses = binding.tvYourExpenses
        tvTotal = binding.tvTotal

        // set onClickListener for buttons to change display type
        val btnMonth = binding.btnMonth
        val btnYear = binding.btnYear

        btnMonth.setOnClickListener {
            changePeriodType("Month", videoMonthPayment, musicMonthPayment, billsMonthPayment, healthcareMonthPayment, othersMonthPayment)
        }

        btnYear.setOnClickListener{
            changePeriodType("Year", videoYearPayment, musicYearPayment, billsYearPayment, healthcareYearPayment, othersYearPayment)
        }


        // fetch data from db and process them for displaying
        val sharedPref =
            this.activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val userId = sharedPref?.getString("UUID", "") ?:""
        db.collection("subscriptions").whereEqualTo("userID", userId).get().addOnSuccessListener { documents ->
            try {
                for (document in documents) {
                    if (document.getString("periodType") == "Month") {
                        when (document.getString("category")) {
                            "Video Streaming" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    videoMonthPayment += temp
                                }
                            }
                            "Music Streaming" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    musicMonthPayment += temp
                                }
                            }
                            "Bills" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    billsMonthPayment += temp
                                }
                            }
                            "Healthcare" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    healthcareMonthPayment += temp
                                }
                            }
                            "Others" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    othersMonthPayment += temp
                                }
                            }
                        }
                    } else if (document.getString("periodType") == "Year") {
                        when (document.getString("category")) {
                            "Video Streaming" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    videoYearPayment += temp
                                }
                            }
                            "Music Streaming" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    musicYearPayment += temp
                                }
                            }
                            "Bills" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    billsYearPayment += temp
                                }
                            }
                            "Healthcare" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    healthcareYearPayment += temp
                                }
                            }
                            "Others" -> {
                                val temp = document.getString("price")
                                    ?.let { Integer.parseInt(it) }
                                if (temp != null) {
                                    othersYearPayment += temp
                                }
                            }
                        }
                    }
                }
                // by default, the app will show the monthly expenses
                changePeriodType("Month", videoMonthPayment, musicMonthPayment, billsMonthPayment, healthcareMonthPayment, othersMonthPayment)

            } catch (ex: Exception) {
                ex.message?.let { Log.e("TAG", it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // function to change the displaying information in dashboard
    private fun changePeriodType(type: String, videoPayment: Float, musicPayment: Float, billsPayment: Float, healthcarePayment: Float, othersPayment: Float) {
        tvYourExpenses.text = "Your ${type}ly Expenses"
        val totalPayment = videoPayment + musicPayment + billsPayment + healthcarePayment + othersPayment
        if (totalPayment == 0f) {
            tvTotal.text = "You have no record here yet..."
            pbVideoStreaming.progress = 0
            pbMusicStreaming.progress = 0
            pbBills.progress = 0
            pbHealthcare.progress = 0
            pbOthers.progress = 0
            return
        }
        tvTotal.text = "Total: ${totalPayment}/${type}"
        pbVideoStreaming.progress = 100
        pbMusicStreaming.progress =
            ((musicPayment + billsPayment + healthcarePayment + othersPayment) / totalPayment * 100).roundToInt()
        pbBills.progress = ((billsPayment + healthcarePayment + othersPayment) /totalPayment * 100).roundToInt()
        pbHealthcare.progress = ((healthcarePayment + othersPayment) / totalPayment * 100).roundToInt()
        pbOthers.progress = ((othersPayment)/ totalPayment * 100).roundToInt()

        tvVideo.text = " --- Video Streaming ${pbVideoStreaming.progress - pbMusicStreaming.progress}% ($${videoPayment})"
        tvMusic.text = " --- Music Streaming ${pbMusicStreaming.progress - pbBills.progress}% ($${musicPayment})"
        tvBills.text = " --- Bills ${pbBills.progress - pbHealthcare.progress}% ($${billsPayment})"
        tvHealthcare.text = " --- Healthcare ${pbHealthcare.progress - pbOthers.progress}% ($${healthcarePayment})"
        tvOthers.text = " --- Others ${pbOthers.progress}% ($${othersPayment})"
    }
}