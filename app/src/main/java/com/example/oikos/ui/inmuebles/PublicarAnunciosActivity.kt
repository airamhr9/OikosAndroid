package com.example.oikos.ui.inmuebles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.oikos.R
import com.google.android.flexbox.FlexboxLayout


class PublicarAnunciosActivity : AppCompatActivity() {

    private val ResultLoadImage = 1
    lateinit var exampleImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publicar_anuncios)

        supportActionBar?.hide()

        val fotoCard = findViewById<CardView>(R.id.foto_card)
        fotoCard.setOnClickListener {
            imageChooser()
        }

        val fotoLayout = findViewById<FlexboxLayout>(R.id.foto_layout)
        //fotoLayout.add
        exampleImage = findViewById(R.id.test_image)
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
            if(selectedImageUri != null)
                exampleImage.setImageURI(selectedImageUri);
        }
    }
}