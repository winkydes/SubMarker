package com.example.submarker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.submarker.R
import com.example.submarker.data.Subscription
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SubscriptionAdapter(private val dataSet: ArrayList<Subscription>) :
    RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSubName: TextView = itemView.findViewById(R.id.tv_subscription_name)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvDaysLeft: TextView = itemView.findViewById(R.id.tv_days_left)

        fun bind(subscription: Subscription) {
            tvSubName.text = subscription.name
            tvCategory.text = subscription.category
            if (subscription.periodType == "Month") {
                val today = LocalDate.now()
                val thisMonth = today.withDayOfMonth(Integer.parseInt(subscription.paymentDate))
                val nextMonth = if (today.isAfter(thisMonth)) {
                    thisMonth.plusMonths(1)
                } else {
                    thisMonth
                }
                tvDaysLeft.text = ChronoUnit.DAYS.between(today, nextMonth).toString()
            } else if (subscription.periodType == "Year") {
                val paymentStr = subscription.paymentDate + "/2023"
                val date = LocalDate.parse(paymentStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                val today = LocalDate.now()
                val thisYear = today.withMonth(date.monthValue).withDayOfMonth(date.dayOfMonth)
                val nextYear = if (today.isAfter(thisYear) || today.isEqual(thisYear)) {
                    thisYear.plusYears(1)
                } else {
                    thisYear
                }
                tvDaysLeft.text = ChronoUnit.DAYS.between(today, nextYear).toString()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.subscription_card, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val data = dataSet[position]
        viewHolder.bind(data)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}