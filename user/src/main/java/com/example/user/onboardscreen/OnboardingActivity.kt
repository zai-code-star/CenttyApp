package com.example.user.onboardscreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.user.R
import com.example.user.login.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var dots: Array<ImageView?>
    private val layouts = intArrayOf(R.layout.slide_item1, R.layout.slide_item2, R.layout.slide_item3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val buttonstar: Button = findViewById(R.id.buttonstar)
        buttonstar.setOnClickListener {
            // Pindah ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup activity Onboarding
        }
        viewPager = findViewById(R.id.viewPager)
        indicatorLayout = findViewById(R.id.indicatorLayout)

        sliderAdapter = SliderAdapter()
        viewPager.adapter = sliderAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        addBottomDots(0)
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts.size)

        indicatorLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            indicatorLayout.addView(dots[i], params)
        }

        if (dots.isNotEmpty()) {
            dots[currentPage]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot))
        }
    }

    private val viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                addBottomDots(position)
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        }

    inner class SliderAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val layout = inflater.inflate(layouts[position], container, false) as ViewGroup

            val imageView = layout.findViewById<ImageView>(R.id.imageView)
            val textViewTitle = layout.findViewById<TextView>(R.id.textViewTitle)
            val textViewDescription = layout.findViewById<TextView>(R.id.textViewDescription)

            when (position) {
                0 -> {
                    imageView.setImageResource(R.drawable.slide2_image)
                    textViewTitle.text = "Cari Rumah Jual atau Sewa?"
                    textViewDescription.text = "Aman Semua Ada, Filter Aja Sesuai Lokasi Anda dan Tipe Properti Apa Yang Anda Cari"
                }
                1 -> {
                    imageView.setImageResource(R.drawable.slide1_image)
                    textViewTitle.text = "Cocok? Tentuin Waktu Survei"
                    textViewDescription.text = "Ga Usah Lama-Lama Kalau Udah Cocok Keburu Ada Yang Ambil"
                }
                2 -> {
                    imageView.setImageResource(R.drawable.slide3_image)
                    textViewTitle.text = "Sering Dapet Respon Lama?"
                    textViewDescription.text = "Disini Ada Fiture Chat Langsung Sama Agent Central Properti"
                }
            }

            container.addView(layout)
            return layout
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }
}
