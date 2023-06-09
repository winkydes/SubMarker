package com.example.submarker.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.submarker.SettingsItem
import com.example.submarker.activities.BugReportActivity
import com.example.submarker.activities.DataRetrievalActivity
import com.example.submarker.activities.NotificationSettingsActivity
import com.example.submarker.databinding.FragmentSettingsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SettingsFragment : Fragment() {
    val db = Firebase.firestore
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val dataRetrievalSetting: SettingsItem = _binding!!.settingsRetrieveData
        dataRetrievalSetting.setOnClickListener{
            val intent = Intent(activity, DataRetrievalActivity::class.java)
            startActivity(intent)
        }

        val notificationSetting: SettingsItem = _binding!!.settingsNotification
        notificationSetting.setOnClickListener{
            val intent = Intent(activity, NotificationSettingsActivity::class.java)
            startActivity(intent)
        }

        val reportBugSetting: SettingsItem = _binding!!.settingsReport
        reportBugSetting.setOnClickListener{
            val intent = Intent(activity, BugReportActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}