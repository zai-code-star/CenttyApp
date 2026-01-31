package com.example.user.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.user.R
import com.example.user.data.model.ListItem
import com.example.user.data.repository.ListingRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*

class ListingFragment : Fragment(), OnItemClickListener {

    private lateinit var viewModel: ListingViewModel
    private lateinit var listingAdapter: ListingAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var btnJual: Button
    private lateinit var btnSewa: Button
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var databaseReference: DatabaseReference
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewListing)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listingAdapter = ListingAdapter(this)
        recyclerView.adapter = listingAdapter

        // Initialize Buttons
        btnJual = view.findViewById(R.id.btnJual)
        btnSewa = view.findViewById(R.id.btnSewa)

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progressBar)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().reference

        // Initialize ViewModel and ViewModelFactory
        val repository = ListingRepository()
        val factory = ListingViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ListingViewModel::class.java)

        // Observer to update data when there are changes
        viewModel.data.observe(viewLifecycleOwner, Observer<List<ListItem>> { items ->
            listingAdapter.submitList(items)
            // Hide ProgressBar when data is loaded
            progressBar.visibility = View.GONE
        })

        // Request data from Firebase when the fragment is created
        viewModel.getAllDataFromFirebase()

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAllDataFromFirebase()
            swipeRefreshLayout.isRefreshing = false
        }

        // Listener for type selection
        btnJual.setOnClickListener {
            filterByType("Jual")
        }

        btnSewa.setOnClickListener {
            filterByType("Sewa")
        }

        // Show ProgressBar when data loading starts
        progressBar.visibility = View.VISIBLE

        return view
    }

    override fun onResume() {
        super.onResume()
        // Request data from Firebase when the fragment is resumed
        viewModel.getAllDataFromFirebase()
    }

    override fun onItemClick(item: ListItem) {
        // Merekam peristiwa "item_click" menggunakan Firebase Analytics
        firebaseAnalytics.logEvent("item_click", null)

        // Membuka DetailActivity ketika item diklik
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("ITEM_UID", item.uid)
        intent.putExtra("ITEM_TYPE", item.tipe_listing) // Mengirim jenis item ke DetailActivity
        startActivity(intent)
    }

    private fun filterByType(type: String) {
        viewModel.getDataFromFirebaseByType(type)
    }
}
