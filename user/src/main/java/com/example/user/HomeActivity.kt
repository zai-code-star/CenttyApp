package com.example.user

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.user.ui.chat.ChatFragment
import com.example.user.ui.dashboard.DashboardFragment
import com.example.user.ui.history.HistoryFragment
import com.example.user.ui.search.ListingFragment
import com.example.user.ui.profile.ProfileFragment
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
            R.id.nav_history -> selectedFragment = HistoryFragment()
            R.id.nav_transaction -> selectedFragment = ProfileFragment()
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
