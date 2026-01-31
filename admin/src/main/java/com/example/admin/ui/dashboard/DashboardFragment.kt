package com.example.admin.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.data.model.DashboardItem
import com.example.admin.data.repository.DashboardRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.NumberFormat
import java.util.*

class DashboardFragment : Fragment() {

    // Deklarasi variabel untuk menyimpan pilihan Tipe Properti dan Kecamatan
    private lateinit var selectedTipeProperti: String
    private lateinit var selectedKecamatan: String

    // Deklarasi elemen UI
    private lateinit var viewModel: DashboardViewModel
    private lateinit var namaEditText: EditText
    private lateinit var hargaEditText: EditText
    private lateinit var alamatEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var uploadButton: Button
    private lateinit var selectedImages: MutableList<Uri>
    private lateinit var recyclerView: RecyclerView
    private lateinit var radioButtonJual: RadioButton
    private lateinit var radioButtonSewa: RadioButton
    private lateinit var spinnerTipeProperti: Spinner
    private lateinit var spinnerKecamatan: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize views
        namaEditText = view.findViewById(R.id.editTextNama)
        hargaEditText = view.findViewById(R.id.editTextHarga)
        alamatEditText = view.findViewById(R.id.editTextAlamat)
        progressBar = view.findViewById(R.id.progressBar)
        uploadButton = view.findViewById(R.id.upload_media_button)
        selectedImages = mutableListOf()
        recyclerView = view.findViewById(R.id.recyclerView)
        radioButtonJual = view.findViewById(R.id.radioButtonJual)
        radioButtonSewa = view.findViewById(R.id.radioButtonSewa)
        spinnerTipeProperti = view.findViewById(R.id.spinnerTipeProperti)
        spinnerKecamatan = view.findViewById(R.id.spinnerKecamatan)
        // Set click listener for upload image button
        uploadButton.setOnClickListener {
            chooseImageFromGallery()
        }


        // Initialize ViewModel
        val repository = DashboardRepository()
        val viewModelFactory = DashboardViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]

        // Set click listener for post button
        view.findViewById<Button>(R.id.post_button).setOnClickListener {
            showLoading()
            Handler(Looper.getMainLooper()).postDelayed({
                savePostToFirebase()
            }, 2000) // Delay 2 seconds before posting to Firebase
        }

        // Add TextWatcher to format nominal harga
        hargaEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Hapus tanda titik dari harga
                val cleanString = s.toString().replace(".", "")

                // Format nominal harga dengan titik sebagai pemisah ribuan
                val formattedPrice = NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(cleanString.toDoubleOrNull() ?: 0)
                hargaEditText.removeTextChangedListener(this)
                hargaEditText.setText(formattedPrice)
                hargaEditText.setSelection(formattedPrice.length)
                hargaEditText.addTextChangedListener(this)
            }
        })

        // Set up spinner for Tipe Properti
        val tipePropertiOptions = arrayOf("Rumah", "Ruko", "Tanah", "Hotel", "Kost")
        val tipePropertiAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipePropertiOptions)
        spinnerTipeProperti.adapter = tipePropertiAdapter
        spinnerTipeProperti.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTipeProperti = tipePropertiOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set up spinner for Kecamatan
        val kecamatanOptions = arrayOf(
            "Ajibarang",
            "Banyumas",
            "Baturraden",
            "Cilongok",
            "Gumelar",
            "Jatilawang",
            "Kebasen",
            "Kalibagor",
            "Karanglewas",
            "Kembaran",
            "Kemranjen",
            "Kedungbanteng",
            "Lumbir",
            "Patikraja",
            "Pekuncen",
            "Purwojati",
            "Purwokerto Barat",
            "Purwokerto Selatan",
            "Purwokerto Timur",
            "Purwokerto Utara",
            "Rawalo",
            "Sokaraja",
            "Somagede",
            "Sumbang",
            "Sumpiuh",
            "Tambak",
            "Wangon"
        )
        val kecamatanAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kecamatanOptions)
        spinnerKecamatan.adapter = kecamatanAdapter
        spinnerKecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedKecamatan = kecamatanOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    // Method to choose image from gallery
    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    if (data?.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                            selectedImages.add(imageUri)
                        }
                    } else if (data?.data != null) {
                        val imageUri: Uri = data.data!!
                        selectedImages.add(imageUri)
                    }
                    Toast.makeText(
                        requireContext(),
                        "${selectedImages.size} gambar dipilih",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Update RecyclerView
                    updateRecyclerView(selectedImages)
                }
            }
        }
    }

    private fun updateRecyclerView(images: List<Uri>) {
        // Update RecyclerView with the selected images
        val adapter = ImageAdapter(images)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun savePostToFirebase() {
        val nama = namaEditText.text.toString()
        val hargaString = hargaEditText.text.toString().replace(".", "") // Hilangkan titik dari harga
        val harga = hargaString.toDoubleOrNull() ?: 0.0 // Konversi ke Double, gunakan nilai default 0.0 jika konversi gagal
        val alamat = alamatEditText.text.toString()

        if (::selectedTipeProperti.isInitialized && ::selectedKecamatan.isInitialized && nama.isNotEmpty() && alamat.isNotEmpty() && selectedImages.isNotEmpty()) {
            val spesifikasi = view?.findViewById<TextInputEditText>(R.id.editTextSpesifikasi)?.text.toString()

            try {
                val storageRef = FirebaseStorage.getInstance().reference

                val uploadTasks = mutableListOf<UploadTask>()
                val downloadUrls = mutableListOf<String>() // List untuk menyimpan tautan download gambar

                for (imageUri in selectedImages) {
                    val imageRef = storageRef.child("images/${UUID.randomUUID()}")
                    val uploadTask = imageRef.putFile(imageUri)

                    // Tambahkan setiap task upload ke list
                    uploadTasks.add(uploadTask)

                    // Ambil tautan download untuk setiap gambar yang diunggah
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            downloadUri?.let {
                                downloadUrls.add(downloadUri.toString())

                                /// Jika semua gambar telah diunggah dan tautan telah didapatkan
                                if (downloadUrls.size == selectedImages.size) {
                                    val downloadUrlsString: List<String> = downloadUrls
                                    val uid = UUID.randomUUID().toString() // Generate UUID untuk item
                                    val posting = DashboardItem(
                                        uid = uid,
                                        nama = nama,
                                        harga = harga,
                                        alamat = alamat,
                                        kecamatan = selectedKecamatan,
                                        tipeProperti = selectedTipeProperti,
                                        spesifikasi = spesifikasi,
                                        uriGambar = downloadUrlsString
                                    )

                                    // Menentukan jenis posting berdasarkan pilihan radio button
                                    val jenisPosting = if (radioButtonJual.isChecked) "Jual" else "Sewa"

                                    // Memanggil metode simpanPosting dengan jenisPosting yang sesuai
                                    viewModel.simpanPosting(posting, jenisPosting)

                                    // Reset UI
                                    namaEditText.text.clear()
                                    hargaEditText.text.clear()
                                    alamatEditText.text.clear()
                                    selectedImages.clear()
                                    updateRecyclerView(selectedImages)


                                    // Tampilkan pesan sukses
                                    Toast.makeText(requireContext(), "Berhasil Terkirim", Toast.LENGTH_SHORT).show()

                                    // Sembunyikan indikator loading setelah selesai
                                    hideLoading()
                                }
                            }
                        } else {
                            // Tangani kesalahan
                            Toast.makeText(requireContext(), "Gagal mengunggah gambar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            hideLoading()
                        }
                    }
                }
            } catch (e: Exception) {
                // Tampilkan pesan kesalahan
                Toast.makeText(requireContext(), "Gagal memposting: ${e.message}", Toast.LENGTH_SHORT).show()
                hideLoading()
            }
        } else {
            // Jika ada field yang kosong atau tidak ada gambar yang dipilih
            Toast.makeText(requireContext(), "Mohon isi semua data", Toast.LENGTH_SHORT).show()
            hideLoading()
        }
    }

    // Show loading indicator
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    // Hide loading indicator
    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}


