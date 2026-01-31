package com.example.admin.ui.transaction

import com.example.admin.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.data.model.TransactionItemJual
import com.example.admin.data.model.TransactionItemSewa
import com.example.admin.data.repository.TransactionRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TransactionFragment : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var overlay: View // Overlay view
    private lateinit var tabLayout: TabLayout
    private lateinit var inputFieldsLayout: LinearLayout
    private lateinit var editTextPropertiTerjual: EditText
    private lateinit var editTextHargaTerjual: EditText
    private lateinit var editTextPropertiTersewa: EditText
    private lateinit var editTextNamaPenyewa: EditText
    private lateinit var editPemilikProperti: EditText
    private lateinit var editPemilikPropertiSewa: EditText
    private lateinit var textViewTotalHarga: TextView
    private lateinit var textInputEditTextTanggalSewa: TextInputEditText
    private lateinit var textInputEditTextTanggalSelesai: TextInputEditText
    private lateinit var textInputEditTextTanggalTerjual: TextInputEditText
    private lateinit var textinputlayouttanggalselesai: TextInputLayout
    private lateinit var editTextBiayaSewa: EditText
    private lateinit var buttonsimpan: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottom_sheet)
        overlay = view.findViewById(R.id.overlay)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Tentukan tinggi maksimum dan minimum bottom sheet
        bottomSheetBehavior.isHideable = true // Biarkan bottom sheet disembunyikan sepenuhnya

        // Inisialisasi TabLayout
        tabLayout = view.findViewById(R.id.tab_layout)

        // Tambahkan tab "Terjual" dan "Tersewa"
        tabLayout.addTab(tabLayout.newTab().setText("Terjual"))
        tabLayout.addTab(tabLayout.newTab().setText("Tersewa"))

        // Inisialisasi komponen
        inputFieldsLayout = view.findViewById(R.id.input_fields_layout)
        editTextPropertiTerjual = view.findViewById(R.id.editTextPropertiTerjual)
        editTextHargaTerjual = view.findViewById(R.id.editTextHargaTerjual)
        editTextPropertiTersewa = view.findViewById(R.id.editTextPropertiTersewa)
        editTextBiayaSewa = view.findViewById(R.id.editTextBiayaSewa)
        editTextNamaPenyewa = view.findViewById(R.id.editTextNamaPenyewa)
        editPemilikProperti = view.findViewById(R.id.editPemilikProperti)
        editPemilikPropertiSewa = view.findViewById(R.id.editPemilikPropertiSewa)
        textinputlayouttanggalselesai = view.findViewById(R.id.text_input_layout_tanggalselesai)

        textInputEditTextTanggalSewa = view.findViewById(R.id.text_input_edit_tanggalsewa)
        textInputEditTextTanggalSelesai = view.findViewById(R.id.text_input_edit_tanggalselesai)
        textInputEditTextTanggalTerjual = view.findViewById(R.id.text_input_date_terjual)
        buttonsimpan = view.findViewById(R.id.button_simpan)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        textViewTotalHarga = view.findViewById(R.id.textViewTotalHarga)

        // Inisialisasi Button
        val buttonTerjual: Button = view.findViewById(R.id.Terjual)
        val buttonSedangDiSewa: Button = view.findViewById(R.id.Sedangdisewa)

        buttonTerjual.setOnClickListener {
            loadTransactions("terjual")
        }

        buttonSedangDiSewa.setOnClickListener {
            loadTransactions("sedang_disewa")
        }

        // Tambahkan listener untuk mendengarkan perubahan status bottom sheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Ketika status bottom sheet berubah
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    // Lakukan sesuatu jika bottom sheet disembunyikan sepenuhnya
                    overlay.visibility =
                        View.GONE // Sembunyikan overlay saat bottom sheet disembunyikan
                } else {
                    overlay.visibility = View.VISIBLE // Tampilkan overlay saat bottom sheet muncul
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Ketika bottom sheet digeser
            }
        })

        // Tambahkan listener untuk tab layout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Ketika tab dipilih, atur visibilitas input fields dan tampilkan datepicker sesuai dengan tab yang dipilih
                when (tab?.position) {
                    0 -> {
                        // Tab "Terjual" dipilih
                        editTextPropertiTerjual.visibility = View.VISIBLE
                        editTextHargaTerjual.visibility = View.VISIBLE
                        editPemilikProperti.visibility = View.VISIBLE
                        textInputEditTextTanggalTerjual.visibility = View.VISIBLE
                        editTextPropertiTersewa.visibility = View.GONE
                        editTextNamaPenyewa.visibility = View.GONE
                        editPemilikPropertiSewa.visibility = View.GONE
                        textInputEditTextTanggalSewa.visibility = View.GONE
                        textInputEditTextTanggalSelesai.visibility = View.GONE
                        textinputlayouttanggalselesai.visibility = View.GONE
                        editTextBiayaSewa.visibility = View.GONE
                    }

                    1 -> {
                        // Tab "Tersewa" dipilih
                        editTextPropertiTerjual.visibility = View.GONE
                        editTextHargaTerjual.visibility = View.GONE
                        editPemilikProperti.visibility = View.GONE
                        textInputEditTextTanggalTerjual.visibility = View.GONE
                        editTextPropertiTersewa.visibility = View.VISIBLE
                        editTextNamaPenyewa.visibility = View.VISIBLE
                        editTextBiayaSewa.visibility = View.VISIBLE
                        editPemilikPropertiSewa.visibility = View.VISIBLE
                        textInputEditTextTanggalSewa.visibility = View.VISIBLE
                        textInputEditTextTanggalSelesai.visibility = View.VISIBLE
                        textinputlayouttanggalselesai.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Tambahkan listener untuk date picker tanggal sewa
        textInputEditTextTanggalSewa.setOnClickListener {
            showDatePicker(textInputEditTextTanggalSewa)
        }

        // Tambahkan listener untuk date picker tanggal selesai
        textInputEditTextTanggalSelesai.setOnClickListener {
            showDatePicker(textInputEditTextTanggalSelesai)
        }

        // Tambahkan listener untuk date picker tanggal terjual
        textInputEditTextTanggalTerjual.setOnClickListener {
            showDatePicker(textInputEditTextTanggalTerjual)
        }

        val fabAdd: FloatingActionButton = view.findViewById(R.id.fab_add)
        fabAdd.setOnClickListener {
            toggleBottomSheet()
        }

        // Set listener untuk tombol simpan
        buttonsimpan.setOnClickListener {
            when (tabLayout.selectedTabPosition) {
                0 -> {
                    // Tab "Terjual" dipilih
                    saveTransaction("jual")
                }

                1 -> {
                    // Tab "Tersewa" dipilih
                    saveTransaction("sewa")
                }
            }
        }

        return view
    }

    private fun updateTotalHarga(transactionType: String, totalHarga: Double) {
        val textViewTotalHarga = view?.findViewById<TextView>(R.id.textViewTotalHarga)

        // Update total harga di FragmentTransaksi
        when (transactionType) {
            "terjual" -> {
                val formattedTotalHarga = totalHarga.formatThousands()
                textViewTotalHarga?.text = "Total  Penjualan: Rp $formattedTotalHarga"
                Log.d("TransactionFragment", "Total Harga Terjual: $formattedTotalHarga")
            }
            "sedang_disewa" -> {
                val formattedTotalHarga = totalHarga.formatThousands()
                textViewTotalHarga?.text = "Total Transaksi Sewa: Rp $formattedTotalHarga"
                Log.d("TransactionFragment", "Total Harga Sewa: $formattedTotalHarga")
            }
            // Add other transaction types if needed
        }
    }

    private fun loadTransactions(transactionType: String) {
        val transactionRepository = TransactionRepository()

        Log.d("TransactionFragment", "Loading transactions for type: $transactionType")

        val branch = if (transactionType == "terjual") "jual" else "sewa"

        transactionRepository.getTransactions(branch) { transactions, totalHarga ->
            // Set adapter untuk RecyclerView berdasarkan jenis transaksi
            when (transactionType) {
                "terjual" -> {
                    val transactionJualAdapter =
                        TransactionJualAdapter(transactions as List<TransactionItemJual>)
                    recyclerView.adapter = transactionJualAdapter
                }
                "sedang_disewa" -> {
                    val transactionSewaAdapter =
                        TransactionSewaAdapter(transactions as List<TransactionItemSewa>)
                    recyclerView.adapter = transactionSewaAdapter
                }
            }

            // Update total harga di FragmentTransaksi
            updateTotalHarga(transactionType, totalHarga)

            Log.d("TransactionFragment", "Loaded ${transactions.size} transactions")
        }
    }


    private fun toggleBottomSheet() {
        if (::overlay.isInitialized) { // Periksa apakah overlay telah diinisialisasi
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        } else {
            // Lakukan penanganan jika overlay belum diinisialisasi
        }
    }

    private fun showDatePicker(textInputEditText: TextInputEditText) {
        val builder = MaterialDatePicker.Builder.datePicker()

        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(selection)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)
            textInputEditText.setText(selectedDate)
        }

        picker.show(childFragmentManager, picker.toString())
    }

    private fun saveTransaction(branch: String) {
        val properti = if (branch == "jual") editTextPropertiTerjual.text.toString().trim()
        else editTextPropertiTersewa.text.toString().trim()
        val pemilik = if (branch == "jual") editPemilikProperti.text.toString().trim()
        else editPemilikPropertiSewa.text.toString().trim()
        val tanggal = if (branch == "jual") textInputEditTextTanggalTerjual.text.toString().trim()
        else null
        val namaPenyewa = if (branch == "sewa") editTextNamaPenyewa.text.toString().trim()
        else null
        val biayaSewa =
            if (branch == "sewa") editTextBiayaSewa.text.toString().toDoubleOrNull() ?: 0.0
            else null
        val tanggalSewa = if (branch == "sewa") textInputEditTextTanggalSewa.text.toString().trim()
        else null
        val tanggalSelesai =
            if (branch == "sewa") textInputEditTextTanggalSelesai.text.toString().trim()
            else null
        val harga =
            if (branch == "jual") editTextHargaTerjual.text.toString().toDoubleOrNull() ?: 0.0
            else 0.0

        // Buat objek TransactionItem berdasarkan cabang yang dipilih
        val transaction = if (branch == "sewa") {
            TransactionItemSewa(
                properti = properti,
                namaPenyewa = namaPenyewa,
                pemilikSewa = pemilik,
                biayaSewa = biayaSewa,
                tanggalSewa = tanggalSewa,
                tanggalSelesai = tanggalSelesai
            )
        } else {
            TransactionItemJual(
                properti = properti,
                harga = harga,
                pemilik = pemilik,
                tanggal = tanggal
            )
        }

        // Simpan transaksi ke Firebase
        val transactionViewModel = TransactionViewModel(TransactionRepository())
        transactionViewModel.saveTransaction(transaction, branch)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Menampilkan pesan toast saat transaksi berhasil disimpan
                    Toast.makeText(
                        requireContext(),
                        "Transaksi berhasil disimpan",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Clear input fields setelah transaksi berhasil disimpan
                    clearInputFields()
                } else {
                    // Menampilkan pesan toast jika terjadi kesalahan saat menyimpan transaksi
                    Toast.makeText(
                        requireContext(),
                        "Gagal menyimpan transaksi",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TransactionFragment", "Error saving transaction", task.exception)
                }
            }
    }

    private fun clearInputFields() {
        // Bersihkan semua input fields di sini
        editTextPropertiTerjual.text.clear()
        editTextHargaTerjual.text.clear()
        editTextPropertiTersewa.text.clear()
        editTextBiayaSewa.text.clear()
        editTextNamaPenyewa.text.clear()
        editPemilikProperti.text.clear()
        editPemilikPropertiSewa.text.clear()
        textInputEditTextTanggalSewa.text?.clear()
        textInputEditTextTanggalSelesai.text?.clear()
        textInputEditTextTanggalTerjual.text?.clear()
    }
}
