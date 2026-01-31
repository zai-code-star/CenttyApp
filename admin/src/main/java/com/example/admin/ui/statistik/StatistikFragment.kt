package com.example.admin.ui.statistik

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.R
import io.supercharge.shimmerlayout.ShimmerLayout

class StatistikFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_statistik, container, false)

        // Find ShimmerLayouts and start shimmer animation
        val shimmerLayout1 = view.findViewById<ShimmerLayout>(R.id.shimmerLayout1)
        shimmerLayout1.startShimmerAnimation()
        val shimmerLayout2 = view.findViewById<ShimmerLayout>(R.id.shimmerLayout2)
        shimmerLayout2.startShimmerAnimation()
        val shimmerLayout3 = view.findViewById<ShimmerLayout>(R.id.shimmerLayout3)
        shimmerLayout3.startShimmerAnimation()
        val shimmerLayout4 = view.findViewById<ShimmerLayout>(R.id.shimmerLayout4)
        shimmerLayout4.startShimmerAnimation()
        val shimmerLayout5 = view.findViewById<ShimmerLayout>(R.id.shimmerLayout5)
        shimmerLayout5.startShimmerAnimation()
        val shimmerLayout6 = view.findViewById<ShimmerLayout>(R.id.shimmerLayout6)
        shimmerLayout6.startShimmerAnimation()
        val shimmerLayout7 = view.findViewById<ShimmerLayout>(R.id.shimmerLayout7)
        shimmerLayout7.startShimmerAnimation()

        // You can repeat the above for other ShimmerLayouts if needed

        return view
    }

    override fun onDestroyView() {
        // Stop shimmer animation when the view is destroyed
        val shimmerLayout1 = view?.findViewById<ShimmerLayout>(R.id.shimmerLayout1)
        shimmerLayout1?.stopShimmerAnimation()
        val shimmerLayout2 = view?.findViewById<ShimmerLayout>(R.id.shimmerLayout2)
        shimmerLayout2?.stopShimmerAnimation()
        val shimmerLayout3 = view?.findViewById<ShimmerLayout>(R.id.shimmerLayout3)
        shimmerLayout3?.stopShimmerAnimation()
        val shimmerLayout4 = view?.findViewById<ShimmerLayout>(R.id.shimmerLayout4)
        shimmerLayout4?.stopShimmerAnimation()
        val shimmerLayout5 = view?.findViewById<ShimmerLayout>(R.id.shimmerLayout5)
        shimmerLayout5?.stopShimmerAnimation()
        val shimmerLayout6 = view?.findViewById<ShimmerLayout>(R.id.shimmerLayout6)
        shimmerLayout6?.stopShimmerAnimation()
        val shimmerLayout7 = view?.findViewById<ShimmerLayout>(R.id.shimmerLayout7)
        shimmerLayout7?.stopShimmerAnimation()

        super.onDestroyView()
    }
}
