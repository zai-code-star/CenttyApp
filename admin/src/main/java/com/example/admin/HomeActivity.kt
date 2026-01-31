package com.example.admin

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.admin.ui.dashboard.DashboardFragment
import com.example.admin.ui.listing.ListingFragment
import com.example.admin.ui.chat.ChatFragment
import com.example.admin.ui.statistik.StatistikFragment
import com.example.admin.ui.transaction.TransactionFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        // Tampilkan dashboard fragment secara default
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DashboardFragment()).commit()
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        var selectedFragment: Fragment? = null

        when (menuItem.itemId) {
            R.id.nav_dashboard -> selectedFragment = DashboardFragment()
            R.id.nav_listing -> selectedFragment = ListingFragment()
            R.id.nav_chat -> selectedFragment = ChatFragment()
            R.id.nav_transaction -> selectedFragment = TransactionFragment()
            R.id.nav_statistik -> selectedFragment = StatistikFragment()
        }

        selectedFragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, it).commit()
            return@OnNavigationItemSelectedListener true
        } ?: return@OnNavigationItemSelectedListener false
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Klik lagi untuk keluar", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}
