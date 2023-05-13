package com.example.submarker.adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.submarker.R
import com.example.submarker.data.Subscription
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class SubscriptionAdapter(private val dataSet: ArrayList<Subscription>, private val mContext: Context) :
    RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSubName: TextView = itemView.findViewById(R.id.tv_subscription_name)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvDaysLeft: TextView = itemView.findViewById(R.id.tv_days_left)
        private val ivImageView: ImageView = itemView.findViewById(R.id.imageView)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvPeriod: TextView = itemView.findViewById(R.id.tv_period)

        private val cardView: CardView = itemView.findViewById(R.id.card_view)

        fun bind(subscription: Subscription, mContext: Context) {
            tvSubName.text = subscription.name
            tvCategory.text = subscription.category
            tvPrice.text = "$" + subscription.price
            tvPeriod.text = "per " + subscription.periodType

            when (subscription.category) {
                "Video Streaming" -> ivImageView.setImageResource(R.drawable.ic_baseline_movie_filter_24)
                "Music Streaming" -> ivImageView.setImageResource(R.drawable.ic_baseline_headphones_24)
                "Bills" -> ivImageView.setImageResource(R.drawable.ic_baseline_receipt_long_24)
                "Healthcare" -> ivImageView.setImageResource(R.drawable.ic_baseline_health_and_safety_24)
                else -> ivImageView.setImageResource(R.drawable.ic_baseline_article_24)
            }
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
                val paymentStr = subscription.paymentDate + "/" + LocalDate.now().year.toString()
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

            when (Integer.parseInt(tvDaysLeft.text.toString())) {
                in 0..5 -> cardView.setBackgroundResource(R.drawable.background_card_urgent)
                in 6..15 -> cardView.setBackgroundResource(R.drawable.background_card_normal)
                else -> cardView.setBackgroundResource(R.drawable.background_card)
            }

            cardView.setOnClickListener {
                // show dialog to perform actions on subscriptions
                val dialog = Dialog(mContext)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.dialog_sub_details)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val tvSubName: TextView = dialog.findViewById(R.id.tv_sub_name)
                val tvCategory: TextView = dialog.findViewById(R.id.tv_category)
                val tvPaymentOn: TextView = dialog.findViewById(R.id.tv_payment_on)
                val tvDelete: TextView = dialog.findViewById(R.id.tv_delete)

                tvSubName.text = subscription.name
                tvCategory.text = subscription.category
                tvPaymentOn.text = "Payment: $${subscription.price}\n paying on ${subscription.paymentDate} each ${subscription.periodType.lowercase(Locale.getDefault())}"

                tvDelete.setOnClickListener {
                    val confirmDeleteDialog = AlertDialog.Builder(mContext)
                    confirmDeleteDialog.apply {
                        setTitle("Are you sure you would like to delete this subscription?")
                        setMessage("Your data will be lost and this action is irreversible.")
                        setNegativeButton("Cancel") { _, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Delete") { _, _ ->
                            val db = Firebase.firestore
                            val sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: null
                            val userId = sharedPref?.getString("UUID", "") ?:""
                            db.collection("subscriptions").whereEqualTo("userID", userId).whereEqualTo("name", subscription.name).whereEqualTo("paymentDate", subscription.paymentDate).get().addOnSuccessListener { documents ->
                                for (document in documents) {
                                    document.reference.delete()
                                }
                            }
                            dialog.dismiss()
                        }
                    }.create().show()
                }
                dialog.show()
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
        viewHolder.bind(data, mContext)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}