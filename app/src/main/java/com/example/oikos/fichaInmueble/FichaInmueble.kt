package com.example.oikos.fichaInmueble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.oikos.R

class FichaInmueble : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha_inmueble)

        supportActionBar?.hide()
    }
}