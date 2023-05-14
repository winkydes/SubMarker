package com.example.submarker.fragments

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submarker.R
import com.example.submarker.adapters.SubscriptionAdapter
import com.example.submarker.data.Subscription
import com.example.submarker.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private val db = Firebase.firestore
    private var _binding: FragmentHomeBinding? = null
    private lateinit var recyclerView: RecyclerView
    private var subscriptionList: ArrayList<Subscription> = ArrayList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup recycler view
        loadSubscriptions()
        recyclerView = binding.subRecyclerView
        recyclerView.adapter = context?.let { SubscriptionAdapter(subscriptionList, it) }
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadSubscriptions()
    }

    private fun loadSubscriptions() {
        val sharedPref =
            this.activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val userId = sharedPref?.getString("UUID", "") ?:""
        db.collection("subscriptions").whereEqualTo("userID", userId).get().addOnSuccessListener { documents ->
            try {
                subscriptionList.clear()
                for (document in documents) {
                    val sub: Subscription = document.toObject(Subscription::class.java)
                    subscriptionList.add(sub)
                }
                if (subscriptionList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.ivEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.ivEmpty.visibility = View.GONE
                }
                recyclerView.adapter?.run {
                    notifyDataSetChanged()
                }
            } catch (ex: Exception) {
                ex.message?.let { Log.e("TAG", it) }
            }
        }
    }
}