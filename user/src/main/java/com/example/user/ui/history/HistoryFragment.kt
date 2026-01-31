package com.example.user.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.user.R
import com.example.user.data.model.ListItem
import com.example.user.data.repository.ListingRepository
import com.example.user.ui.search.DetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.user.ui.search.OnItemClickListener

class HistoryFragment : Fragment(), OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteItems: MutableList<ListItem>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_history, container, false)

        // Initialize RecyclerView
        recyclerView = root.findViewById(R.id.recyclerViewTransactions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoriteItems = mutableListOf()
        favoriteAdapter = FavoriteAdapter(favoriteItems, this) // Tambahkan listener di sini
        recyclerView.adapter = favoriteAdapter

        // Initialize ProgressBar
        progressBar = root.findViewById(R.id.progressBar)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("favorites")
            .child(auth.currentUser?.uid ?: "")

        // Load favorite items
        loadFavoriteItems()

        return root
    }

    private fun loadFavoriteItems() {
        progressBar.visibility = View.VISIBLE // Show ProgressBar when data loading starts

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                favoriteItems.clear() // Clear existing data
                dataSnapshot.children.forEach { itemTypeSnapshot ->
                    val itemType = itemTypeSnapshot.key
                    itemType?.let { type ->
                        itemTypeSnapshot.children.forEach { itemUidSnapshot ->
                            val itemUid = itemUidSnapshot.key
                            itemUid?.let { uid ->
                                retrieveDataFromFirebase(auth.currentUser?.uid, itemType, itemUid)
                            }
                        }
                    }
                }
                progressBar.visibility = View.GONE // Hide ProgressBar when data is loaded
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if needed
                progressBar.visibility = View.GONE // Hide ProgressBar in case of error
                Log.e(
                    "HistoryFragment",
                    "Error dalam mengambil data favorit: ${databaseError.message}"
                )
            }
        })
    }

    private fun retrieveDataFromFirebase(userId: String?, itemType: String?, itemUid: String?) {
        userId?.let { uid ->
            itemType?.let { type ->
                itemUid?.let { uid ->
                    val listingRepository = ListingRepository()
                    listingRepository.getDataFromFirebaseByType(type) { items ->
                        val filteredItem = items.filter { it.uid == uid }
                        if (filteredItem.isNotEmpty()) {
                            val item = filteredItem[0]
                            favoriteItems.add(item)
                            favoriteAdapter.notifyDataSetChanged()

                            // Add log to check if item is successfully added
                            Log.d("HistoryFragment", "Item berhasil ditambahkan: $item")
                        } else {
                            // Add log if item is not found
                            Log.e(
                                "HistoryFragment",
                                "Item dengan UID $uid dan tipe $type tidak ditemukan"
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(item: ListItem) {
        // Membuka DetailActivity ketika item diklik
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("ITEM_UID", item.uid)
        intent.putExtra("ITEM_TYPE", item.tipe_listing) // Mengirim jenis item ke DetailActivity
        startActivity(intent)
    }
}
