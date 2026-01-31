package com.example.admin.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.model.TransactionItemSewa
import com.example.admin.R

class TransactionSewaAdapter(private val transactionList: List<TransactionItemSewa>) :
    RecyclerView.Adapter<TransactionSewaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item_sewa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]

        // Menambahkan penomoran urut pada setiap item
        holder.itemNumber.text = (position + 1).toString()

        holder.propertiTextView.text = "Properti: ${transaction.properti}"
        holder.namaPenyewaTextView.text = "Nama Penyewa    : ${transaction.namaPenyewa ?: "-"}"
        holder.pemilikSewaTextView.text = "Pemilik Properti   : ${transaction.pemilikSewa ?: "-"}"
        holder.biayaSewaTextView.text =   "Biaya Sewa          : ${transaction.biayaSewa?.formatThousands() ?: "-"}"
        holder.tanggalSewaTextView.text =    "Tanggal Sewa : ${transaction.tanggalSewa ?: "-"}"
        holder.tanggalSelesaiTextView.text = "Tanggal Selesai: ${transaction.tanggalSelesai ?: "-"}"
        // Set other TextViews here if needed
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNumber: TextView = itemView.findViewById(R.id.textViewItemNumberSewa)
        val propertiTextView: TextView = itemView.findViewById(R.id.textViewPropertiSewa)
        val namaPenyewaTextView: TextView = itemView.findViewById(R.id.textViewNamaPenyewa)
        val pemilikSewaTextView: TextView = itemView.findViewById(R.id.textViewPemilikSewa)
        val biayaSewaTextView: TextView = itemView.findViewById(R.id.textViewBiayaSewa)
        val tanggalSewaTextView: TextView = itemView.findViewById(R.id.textViewTanggalSewa)
        val tanggalSelesaiTextView: TextView = itemView.findViewById(R.id.textViewTanggalSelesai)
        // Initialize other TextViews here if needed
    }
}
