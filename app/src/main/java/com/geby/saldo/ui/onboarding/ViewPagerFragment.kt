package com.geby.saldo.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.geby.saldo.R
import com.geby.saldo.data.adapter.ViewPagerAdapter
import com.geby.saldo.ui.onboarding.screens.FirstScreen
import com.geby.saldo.ui.onboarding.screens.SecondScreen

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        viewPager.adapter = adapter

        return view
    }
}