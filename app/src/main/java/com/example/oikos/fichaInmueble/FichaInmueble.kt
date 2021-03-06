package com.example.oikos.fichaInmueble

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.oikos.R
import com.google.android.material.tabs.TabLayout


class FichaInmueble : AppCompatActivity() {

    private lateinit var imageViewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha_inmueble)
        supportActionBar?.hide()


        //TODO(cambiar esto por las imágenes que me pasen)
        val images = intArrayOf(R.drawable.viewpager1, R.drawable.viewpager2, R.drawable.viewpager3, R.drawable.viewpager4)
        val adapter = ViewPagerAdapter(this, images)

        imageViewPager = findViewById(R.id.image_viewpager)
        tabLayout = findViewById(R.id.tab_layout)
        imageViewPager.adapter = adapter
        tabLayout.setupWithViewPager(imageViewPager)


    }

}