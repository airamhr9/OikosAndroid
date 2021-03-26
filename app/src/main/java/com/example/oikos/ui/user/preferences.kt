package com.example.oikos.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.OkHttpResponseListener
import com.example.oikos.R
import com.google.android.material.textfield.TextInputEditText
import objects.Preferencia
import okhttp3.Response

class preferences : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var cancelB : AppCompatButton
    lateinit var filterCard : CardView
    lateinit var aceptarB: AppCompatButton
    lateinit var tipoText : TextView
    lateinit var myPreferences : Preferencia

    lateinit var tCiudad : TextInputEditText
    lateinit var tPrecioMax : TextInputEditText
    lateinit var tPrecioMin : TextInputEditText
    lateinit var tHabs : TextInputEditText
    lateinit var tBaño : TextInputEditText
    lateinit var tSupMin : TextInputEditText
    lateinit var tSupMax : TextInputEditText
    lateinit var tGaraje : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        tCiudad = findViewById(R.id.filtro_ciudad)
        tipoText = findViewById(R.id.filter_tipo_text)
        tPrecioMax =  findViewById(R.id.filtro_precio_max)
        tPrecioMin =  findViewById(R.id.filtro_precio_min)
        tHabs =  findViewById(R.id.filtro_habitaciones)
        tBaño =  findViewById(R.id.filtro_baños)
        tSupMin = findViewById(R.id.filtro_superficie_min)
        tSupMax = findViewById(R.id.filtro_superficie_max)
        tGaraje =  findViewById(R.id.filtro_garaje)

        filterCard = findViewById(R.id.filter_search_card)
        myPreferences = intent.getSerializableExtra("preferencias") as Preferencia
        printFilters(myPreferences)

        aceptarB = findViewById(R.id.bAcpetar)
        aceptarB.setOnClickListener{
            editarPreferencias()
            if(myPreferences.superficie_min > myPreferences.superficie_max ){
                AlertDialog.Builder(this@preferences)
                        .setIcon(android.R.drawable.ic_menu_search)
                        .setTitle("Error en superficie")
                        .setMessage("La superficie mínima debe ser menor que la máxima")
                        .setPositiveButton("Ok"
                        ) { _, _ ->}
                        .show()
            }else if(myPreferences.precio_min > myPreferences.precio_max){

                    AlertDialog.Builder(this@preferences)
                            .setIcon(android.R.drawable.ic_menu_search)
                            .setTitle("Error en el precio")
                            .setMessage("El precio mínimo debe ser menor que el máximo")
                            .setPositiveButton("Ok"
                            ) { _, _ ->}
                            .show()
                }else putPreferences()
        }

        val tipoSpinner : AppCompatSpinner = findViewById(R.id.filtro_tipo)
        ArrayAdapter.createFromResource(
                applicationContext,
                R.array.spinner_values,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSpinner.adapter = adapter
        }
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

    private fun editarPreferencias(){
        val cityText = findViewById<TextInputEditText>(R.id.filtro_ciudad).text.toString()
        val precioMin = findViewById<TextInputEditText>(R.id.filtro_precio_min).text.toString()
        val precioMax = findViewById<TextInputEditText>(R.id.filtro_precio_max).text.toString()
        val habs = findViewById<TextInputEditText>(R.id.filtro_habitaciones).text.toString()
        val baños = findViewById<TextInputEditText>(R.id.filtro_baños).text.toString()
        val supMin = findViewById<TextInputEditText>(R.id.filtro_superficie_min).text.toString()
        val supMax = findViewById<TextInputEditText>(R.id.filtro_superficie_max).text.toString()
        val garaje = findViewById<CheckBox>(R.id.filtro_garaje).isChecked

        myPreferences.superficie_min = if( supMin == "" ) 0 else supMin.toInt()
        myPreferences.superficie_max = if(supMax == "") Int.MAX_VALUE else supMax.toInt()
        myPreferences.precio_max =  if(precioMax == "") Double.MAX_VALUE else precioMax.toDouble()
        myPreferences.precio_min =  if( precioMin == "" ) 0.0 else precioMin.toDouble()
        myPreferences.habitaciones = if( habs == "") null else habs.toInt()
        myPreferences.baños = if( habs == "") null else baños.toInt()
        myPreferences.garaje = garaje
        myPreferences.ciudad = cityText
        myPreferences.tipo = tipoText.text.toString()
    }



    private fun putPreferences(){
        editarPreferencias()
            AndroidNetworking.put("http://10.0.2.2:9000/api/preferencias/")
                    .addBodyParameter(myPreferences.toJson())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsOkHttpResponse(object: OkHttpResponseListener{
                        override fun onResponse(response: Response?) {
                            finish()
                        }

                        override fun onError(anError: ANError?) {
                            AlertDialog.Builder(this@preferences)
                                    .setIcon(android.R.drawable.ic_menu_search)
                                    .setTitle("Error al actualizar sus preferencias")
                                    .setMessage("Compruebe que ha introducido bien los datos")
                                    .setPositiveButton("Ok"
                                    ) { _, _ ->}
                                    .show()
                        }


                    })
        }


    private fun printFilters(preferences: Preferencia){
        tCiudad.setText(preferences.ciudad)
        tipoText.text = preferences.tipo
        if(preferences.precio_max != Double.MAX_VALUE)
            tPrecioMax.setText(preferences.precio_max.toString())
        tPrecioMin.setText(preferences.precio_min.toString())
        if(preferences.habitaciones != null)
            tHabs.setText(preferences.habitaciones.toString())
        if(preferences.baños != null)
            tBaño.setText(preferences.baños.toString())
        tSupMin.setText(preferences.superficie_min.toString())
        if(preferences.superficie_max != Int.MAX_VALUE)
            tSupMax.setText(preferences.superficie_max.toString())
        tGaraje.isChecked = preferences.garaje
    }

}
