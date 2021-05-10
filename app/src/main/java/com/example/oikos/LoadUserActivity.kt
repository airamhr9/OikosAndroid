package com.example.oikos

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.Usuario

abstract class LoadUserActivity  : AppCompatActivity(){
    fun loadUser () : Usuario {
        val sharedPref = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val savedUser = (sharedPref?.getString("saved_user", ""))
        val savedJsonUser: JsonObject = JsonParser.parseString(savedUser).asJsonObject
        return Usuario.fromJson(savedJsonUser)
    }
}