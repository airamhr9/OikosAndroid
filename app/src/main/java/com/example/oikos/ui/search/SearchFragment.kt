package com.example.oikos.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.oikos.R
import com.example.oikos.ui.search.localized.LocalizedSearch
import com.google.android.material.textfield.TextInputEditText
import java.util.*


class SearchFragment : Fragment(), AdapterView.OnItemSelectedListener {
    val pisoPos = 0
    val habitacionPos = 1
    val garajePos = 2
    val localPos = 3

    lateinit var expandFiltersButton : LinearLayout
    lateinit var tipoBusqueda : RadioGroup
    lateinit var tipoInmuebleText : AppCompatTextView
    lateinit var cityInputText : TextInputEditText
    lateinit var advancedFiltersLayout : LinearLayout

    lateinit var precioMinText : TextInputEditText
    lateinit var precioMaxText : TextInputEditText
    lateinit var habsText : TextInputEditText
    lateinit var bañosText : TextInputEditText
    lateinit var numCompText : TextInputEditText
    lateinit var supMinText : TextInputEditText
    lateinit var supMaxText : TextInputEditText
    lateinit var garajeCheckbox : CheckBox

    lateinit var numCompLayout : LinearLayout
    lateinit var habsLayout : LinearLayout
    lateinit var bañosLayout : LinearLayout
    lateinit var garajeLayout : LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        initializeFields(root)

        val expandFiltersArrow = root.findViewById<AppCompatImageView>(R.id.filtros_avanzados_arrow)
        expandFiltersButton.setOnClickListener {
            if(advancedFiltersLayout.visibility == View.GONE){
                advancedFiltersLayout.visibility = View.VISIBLE
                expandFiltersArrow.animate().rotation(180f).start();
            } else {
                advancedFiltersLayout.visibility = View.GONE
                expandFiltersArrow.animate().rotation(0f).start();
            }
        }

        tipoBusqueda.check(R.id.alquiler_radio_button)
        val tipoSpinner : AppCompatSpinner = root.findViewById(R.id.filtro_tipo)
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_values,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSpinner.adapter = adapter
        }
        tipoSpinner.onItemSelectedListener = this

        cityInputText = root.findViewById(R.id.filtro_ciudad)

        val mapCard = root.findViewById<CardView>(R.id.search_map_card)
        mapCard.setOnClickListener {
            val intent = Intent(context, LocalizedSearch :: class.java)
            context?.startActivity(intent)
        }

        val searchButton = root.findViewById<AppCompatButton>(R.id.search_button)
        searchButton.setOnClickListener {
            val filters = getFilters(root)
            val intent = Intent(context, SearchResultsActivity :: class.java)
            if(filters.isNotEmpty()){
                intent.putExtra("filters", filters)
                context?.startActivity(intent)
            }
        }

        return root
    }

    private fun initializeFields(view : View){
        precioMinText = view.findViewById(R.id.filtro_precio_min)
        precioMaxText = view.findViewById(R.id.filtro_precio_max)
        habsText = view.findViewById(R.id.filtro_habitaciones)
        bañosText = view.findViewById(R.id.filtro_baños)
        numCompText = view.findViewById(R.id.filtro_num_compas)
        supMinText = view.findViewById(R.id.filtro_superficie_min)
        supMaxText = view.findViewById(R.id.filtro_superficie_max)
        garajeCheckbox = view.findViewById(R.id.filtro_garaje)

        numCompLayout = view.findViewById(R.id.layout_compas)
        habsLayout = view.findViewById(R.id.layout_habs)
        bañosLayout = view.findViewById(R.id.layout_baños)
        garajeLayout = view.findViewById(R.id.layout_garaje)

        expandFiltersButton = view.findViewById(R.id.filtros_avanzados_button)
        advancedFiltersLayout = view.findViewById(R.id.filtros_avanzados_layout)

        tipoBusqueda = view.findViewById(R.id.tipo_busqueda_radio_group)
        tipoInmuebleText = view.findViewById(R.id.filter_tipo_text)
    }

    private fun getFilters(view: View) : HashMap<String, String>{
        val result = hashMapOf<String, String>()

        val cityText = cityInputText.text.toString()
        if(cityText == ""){
            cityInputText.error = "Ciudad es obligatorio"
            return hashMapOf()
        }
        if(cityText != "") result["ciudad"] = cityText
        val modeloInm = tipoInmuebleText.text.toString().toLowerCase(Locale.ROOT)
        result["tipo"] = if (tipoBusqueda.checkedRadioButtonId == R.id.alquiler_radio_button)  "Alquiler" else "Venta"
        result["modelo"] = if(modeloInm == "habitación") "habitacion" else modeloInm

        if(advancedFiltersLayout.visibility == View.VISIBLE){
            val precioMin = precioMinText.text.toString()
            val precioMax = precioMaxText.text.toString()
            val habs = habsText.text.toString()
            val baños = bañosText.text.toString()
            val numComps = numCompText.text.toString()
            val supMin = supMinText.text.toString()
            val supMax = supMaxText.text.toString()
            val garaje = garajeCheckbox.isChecked

            if(precioMin != "") result["precioMin"] = precioMin
            if(precioMax != "") result["precioMax"] = precioMax
            if(result["precioMin"] != null && result["precioMax"] != null){
                if(precioMin.toInt() > precioMax.toInt()){
                    view.findViewById<TextInputEditText>(R.id.filtro_precio_max).error = "Precio mínimo mayor que el máximo"
                    return hashMapOf()
                }
            }
            if(habs != "") result["habitaciones"] = habs
            if(baños != "") result["baños"] = baños
            if(supMin != "") result["supMin"] = supMin
            if(supMax != "") result["supMax"] = supMax
            if(numComps != "") result["numCompañeros"]
            if(result["supMin"] != null && result["supMax"] != null) {
                if (supMin.toInt() > supMax.toInt()) {
                    view.findViewById<TextInputEditText>(R.id.filtro_superficie_max).error = "Superficie mínima mayor que la máxima"
                    return hashMapOf()
                }
            }
            if(modeloInm == "piso" || modeloInm == "habitacion")
                result["garaje"] = garaje.toString()
        }

        return result
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedModel = parent?.getItemAtPosition(position) as String
        tipoInmuebleText.text = selectedModel
        resetModelFilters()
        showModelFilters(position)
    }

    private fun resetModelFilters(){
        habsLayout.visibility = View.GONE
        habsText.setText("")
        tipoBusqueda.getChildAt(0).isEnabled = true
        bañosLayout.visibility = View.GONE
        bañosText.setText("")
        garajeLayout.visibility = View.GONE
        garajeCheckbox.isChecked = false
        numCompLayout.visibility = View.GONE
        numCompText.setText("")
    }

    private fun showModelFilters(position: Int){
        when (position) {
            pisoPos -> {
                habsLayout.visibility = View.VISIBLE
                bañosLayout.visibility = View.VISIBLE
                garajeLayout.visibility = View.VISIBLE
            }
            habitacionPos -> {
                tipoBusqueda.check(R.id.alquiler_radio_button)
                tipoBusqueda.getChildAt(0).isEnabled = false
                bañosLayout.visibility = View.VISIBLE
                numCompLayout.visibility = View.VISIBLE
                garajeLayout.visibility = View.VISIBLE
                habsLayout.visibility = View.VISIBLE
            }
            localPos -> {
                bañosLayout.visibility = View.VISIBLE
            }
        }
    }

     override fun onNothingSelected(parent: AdapterView<*>?) {
         return
    }

}
