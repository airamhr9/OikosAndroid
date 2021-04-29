package com.example.oikos.ui.launcher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.example.oikos.ui.user.registro
import com.example.oikos.ui.user.ui.login.LoginActivity

class Launcher : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)


        val bSiguiente = findViewById<Button>(R.id.bSiguiente)
        bSiguiente.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        val bInmueble = findViewById<Button>(R.id.bInmuebles)
        bInmueble.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}