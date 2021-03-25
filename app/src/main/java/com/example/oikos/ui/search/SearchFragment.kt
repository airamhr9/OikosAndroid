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
import androidx.appcompat.widget.AppCompatSpinner
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
    lateinit var searchResults: ArrayList<DatosInmueble>
    lateinit var customAdapter: CustomAdapter
    lateinit var resultLayout : NestedScrollView
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var mapCard : CardView
    lateinit var seeInMapButton : AppCompatButton

    lateinit var filterCard : CardView
    lateinit var filterButton: AppCompatButton
    lateinit var filterSearchButton : AppCompatButton
    lateinit var tipoText : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)


        filterCard = root.findViewById(R.id.filter_search_card)
        filterCard.visibility = View.INVISIBLE
        filterButton = root.findViewById(R.id.filter_button)
        filterButton.setOnClickListener {
            filterCard.visibility = if (filterCard.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        }
        filterSearchButton = root.findViewById(R.id.button_filter_search)
        val tipoSpinner : AppCompatSpinner = root.findViewById(R.id.filtro_tipo)
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_values,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSpinner.adapter = adapter
        }
        tipoText = root.findViewById(R.id.filter_tipo_text)
        tipoSpinner.onItemSelectedListener = this

        filterSearchButton.setOnClickListener {
            getFilteredResults()
        }

        searchResults = ArrayList()
        loadingCircle = root.findViewById(R.id.loading_search)
        resultLayout = root.findViewById(R.id.results)
        loadingCircle.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE

        getResults()

        val resultsRecycler = root.findViewById<View>(R.id.results_recycler) as RecyclerView
        customAdapter = CustomAdapter(searchResults)
        resultsRecycler.adapter = customAdapter
        // Set layout manager to position the items
        resultsRecycler.layoutManager = LinearLayoutManager(context)

        mapCard = root.findViewById(R.id.search_map_card)
        mapCard.setOnClickListener {
            val intent = Intent(context, LocalizedSearch :: class.java)
            context?.startActivity(intent)
        }

        seeInMapButton = root.findViewById(R.id.see_in_map_button)
        seeInMapButton.isEnabled = false
        seeInMapButton.setOnClickListener {
            (activity as MainActivity).changeToMapFragment(root, searchResults)
        }

        return root
    }

    private fun getResults(){
        if ((activity as MainActivity).isNetworkConnected()) {
            val platformPositioningProvider = PlatformPositioningProvider(requireContext());
            val located = platformPositioningProvider.startLocating(object : PlatformLocationListener {
                override fun onLocationUpdated(location: LocationAndroid?) {
                    val currentLocation = location?.let { convertLocation(it) }
                        AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
                   // AndroidNetworking.put("http://10.0.2.2:9000/api/inmueble/").addBodyParameter(miobjeto.toJason -> JsonObject)
                                .addQueryParameter("coordenada", "true")
                                .addQueryParameter("x", currentLocation?.coordinates?.latitude.toString())
                                .addQueryParameter("y", currentLocation?.coordinates?.longitude.toString())
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsJSONArray(object : JSONArrayRequestListener {
                                    override fun onResponse(response: JSONArray) {
                                        // do anything with response
                                        var i = 0
                                        println("we have response localizada")
                                        searchResults.clear()
                                        while(i < response.length()){
                                            println("here")
                                            println("search result $i ${response[i]}")
                                            searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
                                            i++
                                        }
                                        customAdapter.notifyDataSetChanged()
                                        seeInMapButton.isEnabled = true
                                        loadingCircle.visibility = View.GONE
                                        resultLayout.visibility = View.VISIBLE
                                    }
                                    override fun onError(error: ANError) {
                                        // handle error
                                        println("ERROR: AAAAAAAAA " + error.message)
                                        Toast.makeText(
                                                activity?.applicationContext,
                                                "Error cargando inmuebles",
                                                Toast.LENGTH_LONG
                                        ).show()
                                    }
                                })
                    }
            })
            if(!located){
                if ((activity as MainActivity).isNetworkConnected()) {
                    AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
                            .addQueryParameter("default", "true")
                            .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(object : JSONArrayRequestListener {
                            override fun onResponse(response: JSONArray) {
                                // do anything with response
                                var i = 0
                                println("we have response default")
                                searchResults.clear()
                                while(i < response.length()){
                                    searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
                                    i++
                                }
                                customAdapter.notifyDataSetChanged()
                                seeInMapButton.isEnabled = true
                                loadingCircle.visibility = View.GONE
                                resultLayout.visibility = View.VISIBLE
                            }
                            override fun onError(error: ANError) {
                                Toast.makeText(
                                        activity?.applicationContext,
                                        "Error cargando inmuebles",
                                        Toast.LENGTH_LONG
                                ).show()
                                seeInMapButton.isEnabled = false
                            }
                        })
                }
            }
        } else {
            Toast.makeText(
                    activity?.applicationContext,
                    "Sin conexión a internet",
                    Toast.LENGTH_LONG
            ).show()
            seeInMapButton.isEnabled = false
        }
    }

    private fun getFilterValues(view : View) : MutableMap<String, String>{
        val result = mutableMapOf<String, String>()

        val cityText = view.findViewById<TextInputEditText>(R.id.filtro_ciudad).text.toString()
        val precioMin = view.findViewById<TextInputEditText>(R.id.filtro_precio_min).text.toString()
        val precioMax = view.findViewById<TextInputEditText>(R.id.filtro_precio_max).text.toString()
        val habs = view.findViewById<TextInputEditText>(R.id.filtro_habitaciones).text.toString()
        val baños = view.findViewById<TextInputEditText>(R.id.filtro_baños).text.toString()
        val supMin = view.findViewById<TextInputEditText>(R.id.filtro_superficie_min).text.toString()
        val supMax = view.findViewById<TextInputEditText>(R.id.filtro_superficie_max).text.toString()
        val garaje = view.findViewById<CheckBox>(R.id.filtro_garaje).isChecked

        if(cityText != "") result["ciudad"] = cityText
        if(precioMin != "") result["precioMin"] = precioMin
        if(precioMax != "") result["precioMax"] = precioMax
        if(result["precioMin"] != null && result["precioMax"] != null){
            if(precioMin.toInt() > precioMax.toInt()){
                view.findViewById<TextInputEditText>(R.id.filtro_precio_max).error = "Precio mínimo mayor que el máximo"
                return mutableMapOf()
            }
        }
        if(habs != "") result["habitaciones"] = habs
        if(baños != "") result["baños"] = baños
        if(supMin != "") result["supMin"] = supMin
        if(supMax != "") result["supMax"] = supMax
        if(result["supMin"] != null && result["supMax"] != null) {
            if (supMin.toInt() > supMax.toInt()) {
                view.findViewById<TextInputEditText>(R.id.filtro_superficie_max).error = "Superficie mínima mayor que la máxima"
                return mutableMapOf()
            }
        }
        result["garaje"] = garaje.toString()
        result["tipo"] = tipoText.text.toString()

        return result
    }

    private fun getFilteredResults(){
        filterCard.visibility = View.INVISIBLE
        if ((activity as MainActivity).isNetworkConnected()) {
            val query = AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
            query.addQueryParameter("filtrada", "true")
            val parameters = getFilterValues(requireView())
            val parameterKeys = parameters.keys
            val jsonToSave = JsonObject()
            for (key in parameterKeys) {
                query.addQueryParameter(key, parameters[key])
                jsonToSave.addProperty(key, parameters[key])
            }
            saveSearch(jsonToSave)
            resultLayout.visibility = View.GONE
            loadingCircle.visibility = View.VISIBLE
            seeInMapButton.isEnabled = false
            query.setPriority(Priority.HIGH)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    // do anything with response
                    var i = 0
                    println("we have response")
                    searchResults.clear()
                    while(i < response.length()){
                        println("here")
                        println("search result $i ${response[i]}")
                        searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
                        i++
                    }
                    customAdapter.notifyDataSetChanged()
                    loadingCircle.visibility = View.GONE
                    resultLayout.visibility = View.VISIBLE
                    seeInMapButton.isEnabled = true
                }
                override fun onError(error: ANError) {
                    // handle error
                    println("ERROR: AAAAAAAAA " + error.message)
                    Toast.makeText(
                            activity?.applicationContext,
                            "Error cargando inmuebles",
                            Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                    activity?.applicationContext,
                    "Sin conexión a internet",
                    Toast.LENGTH_LONG
            ).show()
            loadingCircle.visibility = View.GONE
        }
    }

    private fun convertLocation(nativeLocation: LocationAndroid): Location {
        val geoCoordinates = GeoCoordinates(
                nativeLocation.latitude,
                nativeLocation.longitude,
                nativeLocation.altitude)
        val location = Location(geoCoordinates, Date())
        if (nativeLocation.hasBearing()) {
            location.bearingInDegrees = nativeLocation.bearing.toDouble()
        }
        if (nativeLocation.hasSpeed()) {
            location.speedInMetersPerSecond = nativeLocation.speed.toDouble()
        }
        if (nativeLocation.hasAccuracy()) {
            location.horizontalAccuracyInMeters = nativeLocation.accuracy.toDouble()
        }
        return location
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        tipoText.text = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //TODO("Not yet implemented")
    }

    fun saveSearch(jsonObject: JsonObject){
        val sharedPrefs = activity?.getSharedPreferences("prefs", Context.MODE_PRIVATE) ?: return
        with(sharedPrefs.edit()){
            putString("saved_search", jsonObject.toString())
            apply()
            println("COMMITED")
        }
    }

}
