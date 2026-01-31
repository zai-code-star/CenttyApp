package com.example.admin.ui.listing

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.R
import com.example.admin.data.model.ListItem
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*

class ListingAdapter(private val listener: OnItemClickListener) : ListAdapter<ListItem, ListingAdapter.ListingViewHolder>(ListItemDiffCallback()) {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

        // Mendapatkan total view dari Firebase Realtime Database
        getTotalView(currentItem.uid) { totalView ->
            // Update tampilan total view di setiap item RecyclerView
            holder.updateTotalView(totalView)
        }
    }

    inner class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namaTextView: TextView = itemView.findViewById(R.id.namaTextView)
        private val tipePropertiTextView: TextView = itemView.findViewById(R.id.tipePropertiTextView)
        private val hargaTextView: TextView = itemView.findViewById(R.id.hargaTextView)
        private val tipeTextView: TextView = itemView.findViewById(R.id.tipeTextView)
        private val alamatTextView: TextView = itemView.findViewById(R.id.alamatTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewListing)
        private val kecamatanTextView: TextView = itemView.findViewById(R.id.kecamatanTextView)
        private val totalViewTextView: TextView = itemView.findViewById(R.id.totalClicksTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    listener.onItemClick(item)

                    // Merekam peristiwa "item_click" menggunakan Firebase Analytics
                    FirebaseAnalytics.getInstance(itemView.context).logEvent("item_click", null)

                    // Merekam peristiwa "item_click" ke Firebase Realtime Database
                    recordClickEvent(item.uid)
                }
            }
        }

        fun bind(item: ListItem) {
            namaTextView.text = item.nama
            tipeTextView.text = item.tipe_listing
            alamatTextView.text = item.alamat
            tipePropertiTextView.text = item.tipeProperti
            kecamatanTextView.text = item.kecamatan

            // Ubah harga menjadi format dengan pemisah ribuan
            val formattedHarga = String.format("%,.0f", item.harga)
            hargaTextView.text = formattedHarga

            // Memuat gambar menggunakan Glide
            if (!item.uriGambar.isNullOrEmpty()) {
                // Ambil URL gambar pertama dari daftar gambar
                val imageUrl = item.uriGambar[0]
                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)
            } else {
                imageView.setImageDrawable(null)
            }
        }

        // Method untuk mengupdate tampilan total view di setiap item RecyclerView
        fun updateTotalView(totalView: Int) {
            totalViewTextView.text = "Views: $totalView"
        }
    }

    // Merekam peristiwa "item_click" ke Firebase Realtime Database
    private fun recordClickEvent(itemUid: String) {
        val totalViewsRef = databaseReference.child("totalViews").child(itemUid)
        totalViewsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalView = dataSnapshot.getValue(Int::class.java) ?: 0
                totalView++
                totalViewsRef.setValue(totalView)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to record click event: $databaseError")
            }
        })
    }

    // Mendapatkan total view dari Firebase Realtime Database
    private fun getTotalView(itemUid: String, callback: (Int) -> Unit) {
        val totalViewsRef = databaseReference.child("totalViews").child(itemUid)
        totalViewsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val totalView = dataSnapshot.getValue(Int::class.java) ?: 0
                callback(totalView)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to retrieve total view: $databaseError")
            }
        })
    }

    companion object {
        private const val TAG = "ListingAdapter"
    }

    class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}
