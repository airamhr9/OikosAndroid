package com.example.oikos.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.oikos.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {

    private lateinit var homeTabsAdapter : HomeTabsAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeTabsAdapter = HomeTabsAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = homeTabsAdapter

        val tabLayout : TabLayout = view.findViewById(R.id.tab_layout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Última Búsqueda"
                1 -> "Búsquedas Guardadas"
                else -> "Favoritos"
            }
        }.attach()


        //GlobalScope.launch {
          //  ServerConnection().printRequest("http://10.0.2.2:9000/api/hello/")
        //}
    }

}

