package com.example.oikos.ui.user

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.oikos.R

class registro : AppCompatActivity() {

    private val ResultLoadImage = 1
lateinit var nombre : String
lateinit var  correo : String
lateinit var contraseña : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val bSelectImagen = findViewById<Button>(R.id.bSel_imagen)
        bSelectImagen.setOnClickListener {
            imageChooser()

        }
    }



    private fun imageChooser(){
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Seleccione una imagen"), ResultLoadImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ResultLoadImage && resultCode == RESULT_OK && data != null){
            val selectedImageUri : Uri? = data.data
            if(selectedImageUri != null){
                findViewById<ImageView>(R.id.avatar).setImageURI(selectedImageUri)
                }
            }
        }



    fun actualizarDatos(){
        val eName = findViewById<TextView>(R.id.name)
        val eEmail = findViewById<TextView>(R.id.email)
        val ePass = findViewById<TextView>(R.id.password1)
        val ePass2 = findViewById<TextView>(R.id.password2)
        val badPass = findViewById<TextView>(R.id.tBadPass)

        nombre = eName.text.toString()
        correo = eEmail.text.toString()

        //Comprobar contraseña,a usar en otro metodo mejor
        contraseña = ePass.text.toString()
        if(contraseña != ePass2.text.toString()){
            badPass.visibility = View.VISIBLE
        }    }


}