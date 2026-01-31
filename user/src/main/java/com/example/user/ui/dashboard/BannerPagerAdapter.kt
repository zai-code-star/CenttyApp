package com.example.user.ui.dashboard

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.user.R

class BannerPagerAdapter(private val context: Context, private val viewPager: ViewPager) : PagerAdapter() {

    private val images = arrayOf(
        R.drawable.bannerapps,
        R.drawable.bannerapps,
        R.drawable.banner2
    )

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            val currentItem = viewPager.currentItem
            val nextItem = if (currentItem == images.size - 1) 0 else currentItem + 1
            viewPager.setCurrentItem(nextItem, true)
            handler.postDelayed(this, 2000) // Delay 2 detik
        }
    }

    init {
        // Start otomatis geser ketika adapter dibuat
        startAutoScroll()
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_banner, container, false)
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
        imageView.setImageResource(images[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    // Fungsi untuk memulai otomatis geser
    fun startAutoScroll() {
        handler.postDelayed(runnable, 2000) // Mulai dengan delay 2 detik
    }

    // Fungsi untuk menghentikan otomatis geser
    fun stopAutoScroll() {
        handler.removeCallbacks(runnable)
    }
}
