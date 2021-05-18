package com.example.oikos.ui.launcher

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.example.oikos.ui.login.LoginActivity
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.Usuario

class Launcher : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        loadUser()


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

    private fun loadUser(){
        val sharedPref = this@Launcher.getSharedPreferences("user", Context.MODE_PRIVATE)

        val savedUser = (sharedPref?.getString("saved_user", ""))
        println("SAVED USER: $savedUser")
        if(savedUser == "" || savedUser == null){
            val intent = Intent(applicationContext, LoginActivity::class.java)
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
           // finish()
        }else {
            var savedJsonUser: JsonObject = JsonParser.parseString(savedUser).asJsonObject
            var usuario = Usuario.fromJson(savedJsonUser)
            println(usuario.imagen)

            val intent = Intent(applicationContext, MainActivity::class.java)
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
          //  finish()
        }

    }
}