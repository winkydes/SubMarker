package com.example.submarker.adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.submarker.R
import com.example.submarker.data.SuggestedSubscription
import kotlin.reflect.KFunction2

class SuggestedSubscriptionAdapter(private val dataSet: ArrayList<SuggestedSubscription>, private val callBack: KFunction2<String, String, Unit>, private val dialog: Dialog) :
    RecyclerView.Adapter<SuggestedSubscriptionAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val tvSubName: TextView = itemView.findViewById(R.id.tv_subscription_name)
            private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
            private val tvPeriod: TextView = itemView.findViewById(R.id.tv_period)
            fun bind(suggestedSubscription: SuggestedSubscription, callBack: KFunction2<String, String, Unit>, dialog: Dialog) {
                tvSubName.text = suggestedSubscription.name
                tvPrice.text = "$" + suggestedSubscription.price.toString().substringBefore('.')
                tvPeriod.text = "per " + suggestedSubscription.period
                itemView.setOnClickListener{
                    callBack(suggestedSubscription.name, suggestedSubscription.price.toString().substringBefore('.'))
                    dialog.dismiss()
                }
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.suggested_subscription_card, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            val data = dataSet[position]
            viewHolder.bind(data, callBack, dialog)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size
}