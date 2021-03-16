package com.example.oikos.ui.search

import android.opengl.Visibility
import android.location.Location as LocationAndroid
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import com.example.oikos.serverConnection.PlatformPositioningProvider
import com.example.oikos.serverConnection.PlatformPositioningProvider.PlatformLocationListener
import com.google.gson.JsonParser
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Location
import objects.DatosInmueble
import objects.Usuario
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {
    lateinit var searchResults: ArrayList<DatosInmueble>
    lateinit var customAdapter: CustomAdapter
    lateinit var resultLayout : NestedScrollView
    lateinit var loadingCircle : ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        searchResults = ArrayList()
        loadingCircle = root.findViewById(R.id.loading_search)
        resultLayout = root.findViewById(R.id.results)
        loadingCircle.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE

        getResults()

        val resultsRecycler = root.findViewById<View>(R.id.results_recycler) as RecyclerView
        // TODO(Buscar inmuebles)
        customAdapter = CustomAdapter(searchResults)
        resultsRecycler.adapter = customAdapter
        // Set layout manager to position the items
        resultsRecycler.layoutManager = LinearLayoutManager(context)

        return root
    }

    private fun getResults(){
        val platformPositioningProvider = PlatformPositioningProvider(requireContext());
        platformPositioningProvider.startLocating(object : PlatformLocationListener {
            override fun onLocationUpdated(location: LocationAndroid?) {
                 val currentLocation = location?.let { convertLocation(it) }

                    println("after current location")
                if ((activity as MainActivity).isNetworkConnected()) {
                    AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
                            .addQueryParameter("x", currentLocation?.coordinates?.latitude.toString())
                            .addQueryParameter("y", currentLocation?.coordinates?.longitude.toString())
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONArray(object : JSONArrayRequestListener {
                                override fun onResponse(response: JSONArray) {
                                    // do anything with response
                                    var i = 0
                                    println("we have response")
                                    while(i < response.length()){
                                        println("here")
                                        println("search result $i ${response[i]}")
                                        searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
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
                } else {
                    Toast.makeText(
                            activity?.applicationContext,
                            "Sin conexión a internet",
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun convertLocation(nativeLocation: LocationAndroid): Location {
        val geoCoordinates = GeoCoordinates(
                nativeLocation.latitude,
                nativeLocation.longitude,
                nativeLocation.altitude)
        val location: Location = Location(geoCoordinates, Date())
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


}