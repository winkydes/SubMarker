package com.example.submarker.dialogFragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.submarker.R
import com.example.submarker.activities.AddSubscriptionActivity
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var periodType: String;

    companion object {
        fun newInstance(value: String): DatePickerFragment {
            val fragment = DatePickerFragment()
            val args = Bundle()
            args.putString("periodType", value)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        periodType = arguments?.getString("periodType").toString()

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        var dateString = ""
        Log.d("TAG", periodType)
        if (periodType.equals("Month")) {
            dateString = "$day"
        }else if (periodType.equals("Year")) {
            dateString = "${day.toString().padStart(2,'0')}/${(month + 1).toString().padStart(2,'0')}"
        }
        val mActivity:AddSubscriptionActivity = activity as AddSubscriptionActivity
        mActivity.onDateSet(dateString)
    }
}