package com.example.admin.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.model.TransactionItemJual
import com.example.admin.R

class TransactionJualAdapter(private val transactionList: List<TransactionItemJual>) :
    RecyclerView.Adapter<TransactionJualAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item_jual, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]

        // Menambahkan penomoran urut pada setiap item
        holder.itemNumber.text = (position + 1).toString()

        holder.propertiTextView.text = "Properti: ${transaction.properti}"
        holder.hargaTextView.text =    "Harga Terjual     : ${transaction.harga?.formatThousands() ?: "-"}"
        holder.pemilikTextView.text =  "Pemilik Properti : ${transaction.pemilik}"
        holder.tanggalTextView.text =  "Tanggal Terjual: ${transaction.tanggal ?: "Tanggal tidak tersedia"}"
        // Set other TextViews here if needed
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNumber: TextView = itemView.findViewById(R.id.textViewItemNumber)
        val propertiTextView: TextView = itemView.findViewById(R.id.textViewPropertiJual)
        val hargaTextView: TextView = itemView.findViewById(R.id.textViewHarga)
        val pemilikTextView: TextView = itemView.findViewById(R.id.textViewPemilik)
        val tanggalTextView: TextView = itemView.findViewById(R.id.textViewTanggal)
        // Initialize other TextViews here if needed
    }
}
