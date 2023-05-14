package com.example.submarker.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.submarker.activities.AddSubscriptionActivity
import com.example.submarker.data.SuggestedSubscription
import com.example.submarker.databinding.FragmentSuggestionBinding
import com.example.submarker.dialogFragment.DatePickerFragment
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SuggestionFragment : Fragment() {
    val db = Firebase.firestore
    private var _binding: FragmentSuggestionBinding? = null
    private var suggestedSubscriptionList: ArrayList<SuggestedSubscription> = ArrayList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentSuggestionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get 2 random elements from default subscription db and display it to screen
        db.collection("DefaultSubscription").get().addOnSuccessListener { documents ->
            try {
                suggestedSubscriptionList.clear()
                for (document in documents) {
                    val sub: SuggestedSubscription = document.toObject(SuggestedSubscription::class.java)
                    suggestedSubscriptionList.add(sub)
                }
                val randomElements: List<SuggestedSubscription> = suggestedSubscriptionList.asSequence().shuffled().take(2).toList()
                binding.tvTitle1.text = randomElements[0].name
                binding.tvDescription1.text = randomElements[0].description
                binding.tvOffer1.text = "Only $${randomElements[0].price} each ${randomElements[0].period}"
                binding.tvTitle2.text = randomElements[1].name
                binding.tvDescription2.text = randomElements[1].description
                binding.tvOffer2.text = "Only $${randomElements[1].price} each ${randomElements[0].period}"

                binding.btnAdd1.setOnClickListener {
                    val intent = Intent(activity, AddSubscriptionActivity::class.java)
                    intent.putExtra("subscription", randomElements[0])
                    startActivity(intent)
                }

                binding.btnAdd2.setOnClickListener {
                    val intent = Intent(activity, AddSubscriptionActivity::class.java)
                    intent.putExtra("subscription", randomElements[1])
                    startActivity(intent)
                }

            } catch (ex: Exception) {
                ex.message?.let { Log.e("TAG", it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}