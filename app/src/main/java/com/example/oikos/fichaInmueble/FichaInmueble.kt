package com.example.oikos.fichaInmueble

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.ViewPager
import com.example.oikos.R
import com.google.android.material.tabs.TabLayout
import objects.DatosInmueble


class FichaInmueble : AppCompatActivity() {

    private lateinit var imageViewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var datosFicha : DatosInmueble


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha_inmueble)
        supportActionBar?.hide()

        datosFicha = intent.getSerializableExtra("inmueble") as DatosInmueble
        setData(datosFicha)

        val adapter = ViewPagerAdapter(this, datosFicha.images)
        imageViewPager = findViewById(R.id.image_viewpager)
        tabLayout = findViewById(R.id.tab_layout)
        imageViewPager.adapter = adapter
        tabLayout.setupWithViewPager(imageViewPager)
    }

    fun setData(data: DatosInmueble){
        val priceText = findViewById<TextView>(R.id.ficha_precio)
        val addressText = findViewById<TextView>(R.id.ficha_direccion_completa)
        val availableCard = findViewById<CardView>(R.id.ficha_tipo_tarjeta)
        val availableText = findViewById<TextView>(R.id.ficha_tipo_texto)
        val numBaths = findViewById<TextView>(R.id.ficha_baños)
        val numRooms = findViewById<TextView>(R.id.ficha_habitaciones)
        val size = findViewById<TextView>(R.id.ficha_superficie)
        val hasGarage = findViewById<TextView>(R.id.ficha_garaje)
        val description = findViewById<TextView>(R.id.ficha_descripcion)
        val landlordName = findViewById<TextView>(R.id.ficha_propietario_name)
        val landlordMail = findViewById<TextView>(R.id.ficha_propietario_mail)

        priceText.text = "${data.price}€"
        addressText.text = "${data.direccion}"
        if(data.tipo == "Alquiler") {
            //TODO(cambiar a valores en Res/Values)
            availableCard.setCardBackgroundColor(Color.parseColor("#4caf50"))
        } else {
            //TODO(cambiar a valores en Res/Values)
            availableCard.setCardBackgroundColor(Color.parseColor("#c62828"))
        }
        availableText.text = data.tipo
        numBaths.text = "Baños: ${data.baños}"
        numRooms.text = "Habitaciones: ${data.habitaciones}"
        size.text = "Superficie: ${data.superficie}m\u00B2"
        hasGarage.text = if(data.garaje) "Garaje: Sí" else "Garaje: No"
        description.text = data.descripcion
        landlordName.text = data.nombrePropietario
        landlordMail.text = data.mailPropietario
    }

    fun sendMail(view: View){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(datosFicha.mailPropietario))
        i.putExtra(Intent.EXTRA_SUBJECT, "Contacto por el piso de ${datosFicha.direccion}")
        try {
            startActivity(Intent.createChooser(i, "Enviar correo..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No hay clientes de correo instalados", Toast.LENGTH_SHORT).show()
        }
    }

    fun backButtonPressed(view : View){
        super.onBackPressed()
    }

}