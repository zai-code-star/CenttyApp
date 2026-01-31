package com.example.user.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.user.R
import com.example.user.data.model.ListItem
import com.example.user.ui.search.OnItemClickListener

class FavoriteAdapter(private val favoriteItems: List<ListItem>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.namaTextView)
        val itemTypeTextView: TextView = itemView.findViewById(R.id.tipePropertiTextView)
        val itemPriceTextView: TextView = itemView.findViewById(R.id.hargaTextView)
        val itemAddressTextView: TextView = itemView.findViewById(R.id.alamatTextView)
        val itemDistrictTextView: TextView = itemView.findViewById(R.id.kecamatanTextView)
        val itemImageView: ImageView = itemView.findViewById(R.id.imageViewListing)
        val tipeTextView: TextView = itemView.findViewById(R.id.tipeTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = favoriteItems[position]
                    listener.onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = favoriteItems[position]

        // Bind data ke view holder
        holder.itemNameTextView.text = currentItem.nama
        holder.itemTypeTextView.text = "${currentItem.tipeProperti}"
        val formattedHarga = String.format("%,.0f", currentItem.harga)
        holder.itemPriceTextView.text = formattedHarga
        holder.itemAddressTextView.text = "${currentItem.alamat}"
        holder.itemDistrictTextView.text = "${currentItem.kecamatan}"
        holder.tipeTextView.text = "${currentItem.tipe_listing}"

        // Load gambar menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.uriGambar?.get(0)) // Mengambil URL gambar pertama dari list URL
            .into(holder.itemImageView) // Memuat gambar ke ImageView
    }

    override fun getItemCount(): Int {
        return favoriteItems.size
    }
}
