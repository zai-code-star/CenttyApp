package com.example.user.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.user.R
import com.example.user.data.model.DashboardItem
import com.example.user.data.repository.DashboardRepository
import com.example.user.ui.dashboard.blog.FlowBeliActivity
import com.example.user.ui.dashboard.blog.FlowSewaActivity
import com.example.user.ui.dashboard.blog.TipsActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DashboardFragment : Fragment() {
    private lateinit var currentUser: FirebaseUser
    private lateinit var profileImage: ShapeableImageView
    private lateinit var username: TextView
    private lateinit var viewPager: ViewPager
    private lateinit var bannerPagerAdapter: BannerPagerAdapter
    private lateinit var bannerBox: RelativeLayout
    private lateinit var closeBanner: ImageView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var dashboardItemAdapter1: DashboardItemAdapter
    private lateinit var dashboardItemAdapter2: DashboardItemAdapter
    private lateinit var dashboardRepository: DashboardRepository
    private lateinit var firebaseAnalytics: FirebaseAnalytics // Tambahkan inisialisasi FirebaseAnalytics
    private lateinit var propertyValueInput: EditText
    private lateinit var taxRateInput: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var tipsAndTricksButton: ImageButton
    private lateinit var buttonalurpembelian: ImageButton
    private lateinit var buttonalursewa: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext()) // Inisialisasi firebaseAnalytics

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Inisialisasi UI
        propertyValueInput = view.findViewById(R.id.editTextPropertyValue)
        taxRateInput = view.findViewById(R.id.editTextTaxRate)
        calculateButton = view.findViewById(R.id.buttonCalculate)
        resultTextView = view.findViewById(R.id.textViewResult)
        tipsAndTricksButton = view.findViewById(R.id.tipsAndTricksButton)
        buttonalurpembelian = view.findViewById(R.id.buttonalurpembelian)
        buttonalursewa = view.findViewById(R.id.buttonalursewa)

        // Set onClickListener untuk tombol hitung
        calculateButton.setOnClickListener {
            calculatePropertyTax()
        }
        // Set click listener to navigate to TipsAndTricksActivity
        tipsAndTricksButton.setOnClickListener {
            val intent = Intent(requireContext(), TipsActivity::class.java)
            startActivity(intent)
        }
        // Set click listener to navigate to TipsAndTricksActivity
        buttonalurpembelian.setOnClickListener {
            val intent = Intent(requireContext(), FlowBeliActivity::class.java)
            startActivity(intent)
        }
        // Set click listener to navigate to TipsAndTricksActivity
        buttonalursewa.setOnClickListener {
            val intent = Intent(requireContext(), FlowSewaActivity::class.java)
            startActivity(intent)
        }

        profileImage = view.findViewById(R.id.profileImage)
        username = view.findViewById(R.id.username)
        viewPager = view.findViewById(R.id.viewPager)

        bannerPagerAdapter = BannerPagerAdapter(requireContext(), viewPager)
        viewPager.adapter = bannerPagerAdapter

        bannerBox = view.findViewById(R.id.bannerBox)
        closeBanner = view.findViewById(R.id.closeBanner)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        // Initialize RecyclerViews
        recyclerView1 = view.findViewById(R.id.recyclerView1)
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())


        // Tambahkan parameter listener saat membuat instance DashboardItemAdapter
        dashboardItemAdapter1 = DashboardItemAdapter(emptyList(), object : DashboardItemAdapter.OnItemClickListener {
            override fun onItemClick(item: DashboardItem) {
                // Merekam peristiwa "item_click" menggunakan Firebase Analytics
                firebaseAnalytics.logEvent("item_click", null)

                // Membuka DetailActivity ketika item diklik
                val intent = Intent(requireContext(), DetailItem::class.java)
                intent.putExtra("ITEM_UID", item.uid)
                intent.putExtra("ITEM_TYPE", item.tipeProperti)
                startActivity(intent)
            }
        })
        recyclerView1.adapter = dashboardItemAdapter1

        recyclerView2 = view.findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        // Tambahkan parameter listener saat membuat instance DashboardItemAdapter
        dashboardItemAdapter2 = DashboardItemAdapter(emptyList(), object : DashboardItemAdapter.OnItemClickListener {
            override fun onItemClick(item: DashboardItem) {
                // Merekam peristiwa "item_click" menggunakan Firebase Analytics
                firebaseAnalytics.logEvent("item_click", null)

                // Membuka DetailActivity ketika item diklik
                val intent = Intent(requireContext(), DetailItem::class.java)
                intent.putExtra("ITEM_UID", item.uid)
                intent.putExtra("ITEM_TYPE", item.tipeProperti)
                startActivity(intent)
            }
        })
        recyclerView2.adapter = dashboardItemAdapter2

        // Initialize repository
        dashboardRepository = DashboardRepository()

        // Get current user from Firebase Authentication
        val firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        // Set user information to views
        username.text = currentUser.displayName

        closeBanner.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE

            Handler().postDelayed({
                bannerBox.visibility = View.GONE
                loadingProgressBar.visibility = View.GONE
            }, 2000)
        }

        if (!PreferenceManager.isBannerShown(requireContext())) {
            bannerBox.visibility = View.VISIBLE
            PreferenceManager.setIsBannerShown(requireContext(), true)
        } else {
            bannerBox.visibility = View.GONE
        }

        // Request location permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // Request notification permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }

        return view
    }

    // Metode untuk menghitung pajak properti
    private fun calculatePropertyTax() {
        // Mendapatkan nilai input dari EditText
        val propertyValueStr = propertyValueInput.text.toString()
        val taxRateStr = taxRateInput.text.toString()

        // Validasi input
        if (propertyValueStr.isBlank() || taxRateStr.isBlank()) {
            resultTextView.text = "Please fill in both fields"
            return
        }

        // Mendapatkan nilai property value dan tax rate dari input
        val propertyValue = propertyValueStr.toDouble()
        val taxRate = taxRateStr.toDouble()

        // Menghitung pajak properti
        val propertyTax = propertyValue * (taxRate / 100)

        // Menampilkan hasil ke TextView
        val formattedHarga = String.format("%,.0f", propertyTax)
        resultTextView.text = formattedHarga
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Mengambil URL gambar profil pengguna dari Firebase Authentication
        val photoUrl = currentUser.photoUrl

        // Memuat gambar profil menggunakan Glide
        Glide.with(this)
            .load(photoUrl)
            .placeholder(R.drawable.userprofile) // Placeholder image saat gambar sedang dimuat
            .error(R.drawable.userprofile) // Gambar yang akan ditampilkan jika terjadi kesalahan
            .into(profileImage)

        // Set layout manager for recyclerView1 (horizontal orientation)
        recyclerView1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Set layout manager for recyclerView2 (horizontal orientation)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        bannerPagerAdapter.startAutoScroll()

        // Fetch data for recyclerView1 (based on tipeProperti)
        fetchDataForRecyclerView1("Rumah")

        // Fetch data for recyclerView2 (based on kecamatan)
        fetchDataForRecyclerView2("Purwokerto Barat")

        // Set tab listeners
        setTabListeners()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerPagerAdapter.stopAutoScroll()
    }
    private fun fetchDataForRecyclerView1(tipeProperti: String) {
        dashboardRepository.getDashboardItemsByType(tipeProperti) { items ->
            Log.d("DashboardFragment", "Items for RecyclerView 1: $items")
            dashboardItemAdapter1.updateData(items)
        }
    }

    private fun fetchDataForRecyclerView2(kecamatan: String) {
        dashboardRepository.getDashboardItemsByLocation(kecamatan) { items ->
            Log.d("DashboardFragment", "Items for RecyclerView 2: $items")
            dashboardItemAdapter2.updateData(items)
        }
    }

    private fun setTabListeners() {
        // Set click listeners for tipeProperti tabs
        // Rumah
        view?.findViewById<TextView>(R.id.tabRumah)?.setOnClickListener {
            Log.d("DashboardFragment", "Tab Rumah clicked")
            fetchDataForRecyclerView1("Rumah")
        }

        // Ruko
        view?.findViewById<TextView>(R.id.tabRuko)?.setOnClickListener {
            Log.d("DashboardFragment", "Tab Ruko clicked")
            fetchDataForRecyclerView1("Ruko")
        }

        // Tanah
        view?.findViewById<TextView>(R.id.tabTanah)?.setOnClickListener {
            Log.d("DashboardFragment", "Tab Tanah clicked")
            fetchDataForRecyclerView1("Tanah")
        }

        // Set click listeners for kecamatan tabs
        // PurwokertoUtara
        view?.findViewById<TextView>(R.id.tabPurwokertoUtara)?.setOnClickListener {
            Log.d("DashboardFragment", "Tab Purwokerto Utara clicked")
            fetchDataForRecyclerView2("purwokertoutara")
        }

        // PurwokertoTimur
        view?.findViewById<TextView>(R.id.tabPurwokertoTimur)?.setOnClickListener {
            Log.d("DashboardFragment", "Tab Purwokerto Timur clicked")
            fetchDataForRecyclerView2("Purwokerto Timur")
        }

        // PurwokertoSelatan
        view?.findViewById<TextView>(R.id.tabPurwokertoSelatan)?.setOnClickListener {
            Log.d("DashboardFragment", "Tab Purwokerto Selatan clicked")
            fetchDataForRecyclerView2("Purwokerto Utara")
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
    }
}

