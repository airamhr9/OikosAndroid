package com.example.oikos.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import com.example.oikos.R
import com.google.gson.JsonObject

class preferences : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var cancelB : AppCompatButton
    lateinit var filterCard : CardView
    lateinit var aceptarB: AppCompatButton
    lateinit var tipoText : TextView








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        filterCard = findViewById(R.id.filter_search_card)


        aceptarB = findViewById(R.id.bAcpetar)

        val tipoSpinner : AppCompatSpinner = findViewById(R.id.filtro_tipo)
        ArrayAdapter.createFromResource(
                applicationContext,
                R.array.spinner_values,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSpinner.adapter = adapter
        }
        tipoText = findViewById(R.id.filter_tipo_text)
        tipoSpinner.onItemSelectedListener = this




        cancelB = findViewById(R.id.bCancelar)
        cancelB.setOnClickListener{
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        tipoText.text = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }




}
