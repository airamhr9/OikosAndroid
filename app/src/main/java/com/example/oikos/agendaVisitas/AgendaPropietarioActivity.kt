package com.example.oikos.agendaVisitas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.oikos.R
import objects.DatosInmueble
import objects.InmuebleWithModelo

class AgendaPropietarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_propietario)

        val inmueble = intent.getSerializableExtra("inmueble") as DatosInmueble
        val modelo = intent.getSerializableExtra("modelo") as String

        supportActionBar?.hide()
        var title = findViewById<TextView>(R.id.publicar_toolbar_text)
        title.text = "Agenda de $modelo en ${inmueble.direccion}"
    }

    fun onBackPressed(view: View) {
        super.onBackPressed()
    }
}