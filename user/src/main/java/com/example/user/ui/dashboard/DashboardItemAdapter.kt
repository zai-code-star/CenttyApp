package com.example.user.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.user.R
import com.example.user.data.model.DashboardItem
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*

class DashboardItemAdapter(private var items: List<DashboardItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<DashboardItemAdapter.ViewHolder>() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun updateData(newItems: List<DashboardItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.namaTextView)
        private val hargaTextView: TextView = itemView.findViewById(R.id.hargaTextView)
        private val kecamatanTextView: TextView = itemView.findViewById(R.id.kecamatanTextView)
        private val tipePropertiTextView: TextView =
            itemView.findViewById(R.id.tipePropertiTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewListing)
        private val tipeTextView: TextView = itemView.findViewById(R.id.tipeTextView)


        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    listener.onItemClick(item)

                    // Merekam peristiwa "item_click" menggunakan Firebase Analytics
                    FirebaseAnalytics.getInstance(itemView.context).logEvent("item_click", null)

                    // Merekam peristiwa "item_click" ke Firebase Realtime Database
                    recordClickEvent(item.uid)
                }
            }
        }

        fun bind(item: DashboardItem) {
            nameTextView.text = item.nama
            tipeTextView.text = item.tipe_listing
            val formattedHarga = String.format("%,.0f", item.harga)
            hargaTextView.text = formattedHarga
            kecamatanTextView.text = item.kecamatan
            tipePropertiTextView.text = item.tipeProperti

            if (!item.uriGambar.isNullOrEmpty()) {
                val imageUrl = item.uriGambar[0]
                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)
            } else {
                imageView.setImageDrawable(null)
            }
        }
    }

    private fun recordClickEvent(itemUid: String) {
        val totalViewsRef = databaseReference.child("totalViews").child(itemUid)
        totalViewsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalView = dataSnapshot.getValue(Int::class.java) ?: 0
                totalView++
                totalViewsRef.setValue(totalView)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    interface OnItemClickListener {
        fun onItemClick(item: DashboardItem)
    }
}
