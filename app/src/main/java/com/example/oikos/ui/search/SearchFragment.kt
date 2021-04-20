package com.example.oikos.ui.search

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.location.Location as LocationAndroid
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
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmuebleActivity
import com.example.oikos.serverConnection.PlatformPositioningProvider
import com.example.oikos.serverConnection.PlatformPositioningProvider.PlatformLocationListener
import com.example.oikos.ui.search.localized.LocalizedSearch
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Location
import objects.DatosInmueble
import objects.Preferencia
import objects.Usuario
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment(), AdapterView.OnItemSelectedListener {
    lateinit var expandFiltersButton : LinearLayout
    lateinit var tipoBusqueda : RadioGroup
    lateinit var tipoInmuebleText : AppCompatTextView
    lateinit var cityInputText : TextInputEditText
    lateinit var advancedFiltersLayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        */
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.nueva_busqueda, container, false)

        expandFiltersButton = root.findViewById(R.id.filtros_avanzados_button)
        advancedFiltersLayout = root.findViewById(R.id.filtros_avanzados_layout)
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
        tipoBusqueda = root.findViewById(R.id.tipo_busqueda_radio_group)
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
        tipoInmuebleText = root.findViewById(R.id.filter_tipo_text)
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
            intent.putExtra("filters", filters)
            context?.startActivity(intent)
        }

        return root
    }

    private fun getFilters(view: View) : HashMap<String, String>{
        val result = hashMapOf<String, String>()

        val cityText = cityInputText.text.toString()
        if(cityText != "") result["ciudad"] = cityText
        result["modelo"] = tipoInmuebleText.text.toString()
        result["tipo"] = if (tipoBusqueda.checkedRadioButtonId == R.id.alquiler_radio_button)  "Alquiler" else "Venta"

        if(advancedFiltersLayout.visibility == View.VISIBLE){
            val precioMin = view.findViewById<TextInputEditText>(R.id.filtro_precio_min).text.toString()
            val precioMax = view.findViewById<TextInputEditText>(R.id.filtro_precio_max).text.toString()
            val habs = view.findViewById<TextInputEditText>(R.id.filtro_habitaciones).text.toString()
            val baños = view.findViewById<TextInputEditText>(R.id.filtro_baños).text.toString()
            val supMin = view.findViewById<TextInputEditText>(R.id.filtro_superficie_min).text.toString()
            val supMax = view.findViewById<TextInputEditText>(R.id.filtro_superficie_max).text.toString()
            val garaje = view.findViewById<CheckBox>(R.id.filtro_garaje).isChecked
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
            if(result["supMin"] != null && result["supMax"] != null) {
                if (supMin.toInt() > supMax.toInt()) {
                    view.findViewById<TextInputEditText>(R.id.filtro_superficie_max).error = "Superficie mínima mayor que la máxima"
                    return hashMapOf()
                }
            }
            result["garaje"] = garaje.toString()
        }

        return result
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        tipoInmuebleText.text = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    /*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GestionInmuebleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GestionInmuebleFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
     */


}
