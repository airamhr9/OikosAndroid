package com.example.oikos.ui.inmuebles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.oikos.R

class PublicarAnunciosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publicar_anuncios)

        supportActionBar?.hide()
    }
}