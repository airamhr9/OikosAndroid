package com.example.oikos.ui.home

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.example.oikos.ui.search.CustomAdapter
import com.example.oikos.ui.user.UserViewModel
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.here.sdk.core.GeoCoordinates
import objects.DatosInmueble
import objects.InmuebleFactory
import objects.Preferencia
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class RecommendedFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    lateinit var searchResults: ArrayList<DatosInmueble>
    lateinit var customAdapter: CustomAdapter
    lateinit var resultLayout : NestedScrollView
    lateinit var loadingCircle : ContentLoadingProgressBar

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_recommended, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchResults = ArrayList()
        loadingCircle = view.findViewById(R.id.loading_search_recommended)
        resultLayout = view.findViewById(R.id.results_recommended)
        loadingCircle.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE

        val resultsRecycler = view.findViewById<View>(R.id.results_recommended_recycler) as RecyclerView
        //customAdapter = CustomAdapter(searchResults)
        //resultsRecycler.adapter = customAdapter
        resultsRecycler.layoutManager = LinearLayoutManager(context)

        if ((activity as MainActivity).isNetworkConnected()) {
            AndroidNetworking.get("http://10.0.2.2:9000/api/preferencias/")
                    .addQueryParameter("id", "1")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            val jsonPreferences = JsonParser.parseString(response.toString()).asJsonObject
                            if(jsonPreferences.keySet().isEmpty()) {
                                getResults()
                            }
                            else {
                                getRecommended(jsonPreferences)
                            }
                        }
                        override fun onError(error: ANError) {
                            Toast.makeText(
                                    activity?.applicationContext,
                                    "Compruebe la conexión a internet",
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
        }
    }

    private fun getResults(){
        if ((activity as MainActivity).isNetworkConnected()) {
            val platformPositioningProvider = PlatformPositioningProvider(requireContext());
            val located = platformPositioningProvider.startLocating(object : PlatformPositioningProvider.PlatformLocationListener {
                override fun onLocationUpdated(location: Location?) {
                    val currentLocation = location?.let { convertLocation(it) }
                    AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
                            .addQueryParameter("coordenada", "true")
                            .addQueryParameter("x", currentLocation?.coordinates?.latitude.toString())
                            .addQueryParameter("y", currentLocation?.coordinates?.longitude.toString())
                            .setPriority(Priority.HIGH)
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
                                        //searchResults.add(InmuebleFactory.new(JsonParser.parseString(response[i].toString()).asJsonObject))
                                        i++
                                    }
                                    customAdapter.notifyDataSetChanged()
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
                                    println("we have response")
                                    searchResults.clear()
                                    while(i < response.length()){
                                        //searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
                                        i++
                                    }
                                    customAdapter.notifyDataSetChanged()
                                    loadingCircle.visibility = View.GONE
                                    resultLayout.visibility = View.VISIBLE
                                }
                                override fun onError(error: ANError) {
                                    Toast.makeText(
                                            activity?.applicationContext,
                                            "Error cargando inmuebles",
                                            Toast.LENGTH_LONG
                                    ).show()
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
        }
    }

    private fun convertLocation(nativeLocation: Location): com.here.sdk.core.Location {
        val geoCoordinates = GeoCoordinates(
                nativeLocation.latitude,
                nativeLocation.longitude,
                nativeLocation.altitude)
        val location = com.here.sdk.core.Location(geoCoordinates, Date())
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

    fun getRecommended(preferences : JsonObject){
        preferences.remove("usuario")
        preferences.remove("id")
        val query = AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
        query.addQueryParameter("filtrada", "true")
        val keySet = preferences.keySet()
        for(key in keySet){
            println("$key: ${preferences.get(key).asString}")
            query.addQueryParameter(key, preferences.get(key).asString)
        }
        resultLayout.visibility = View.GONE
        loadingCircle.visibility = View.VISIBLE
        query.setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        var i = 0
                        println("we have response lenght: ${response.length()}")
                        searchResults.clear()
                        while(i < response.length()){
                            println("here")
                            println("search result $i ${response[i]}")
                            //searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
                            i++
                        }
                        customAdapter.notifyDataSetChanged()
                        loadingCircle.visibility = View.GONE
                        resultLayout.visibility = View.VISIBLE
                    }
                    override fun onError(error: ANError) {
                        println(error.message)
                        Toast.makeText(
                                activity?.applicationContext,
                                "Error cargando inmuebles",
                                Toast.LENGTH_LONG
                        ).show()
                        loadingCircle.visibility = View.GONE
                    }
                })
    }
}


