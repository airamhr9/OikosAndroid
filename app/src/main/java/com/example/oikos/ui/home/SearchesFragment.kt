package com.example.oikos.ui.home

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.oikos.serverConnection.PlatformPositioningProvider
import com.example.oikos.ui.search.CustomAdapter
import com.example.oikos.ui.user.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.here.sdk.core.GeoCoordinates
import objects.Busqueda
import objects.DatosInmueble
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class SearchesFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    lateinit var searchResults: ArrayList<Busqueda>
    lateinit var adapter : SearchAdapter
    lateinit var resultLayout : NestedScrollView
    lateinit var loadingCircle : ContentLoadingProgressBar

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_searches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchResults = ArrayList()
        loadingCircle = view.findViewById(R.id.loading_searches)
        resultLayout = view.findViewById(R.id.results_searches)
        loadingCircle.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE

        val resultsRecycler = view.findViewById<View>(R.id.results_searches_recycler) as RecyclerView
        adapter = SearchAdapter(searchResults)
        resultsRecycler.adapter = adapter
        resultsRecycler.layoutManager = LinearLayoutManager(context)

        getResults()
    }

    private fun getResults(){
        if ((activity as MainActivity).isNetworkConnected()) {
            AndroidNetworking.get("http://10.0.2.2:9000/api/preferencias/")
                    .addQueryParameter("id", "1")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                            val jsonPreferences = JsonParser.parseString(response.toString()).asJsonObject
                            if(jsonPreferences.keySet().isEmpty()) {
                                //getResults()
                            }
                            else {
                                getRecommended(jsonPreferences)
                            }
                        }
                        override fun onError(error: ANError) {
                            Snackbar.make(
                                    requireView(),
                                    "Compruebe la conexión a internet",
                                    Snackbar.LENGTH_LONG
                            ).show()
                        }
                    })
        } else {
            Snackbar.make(
                    requireView(),
                    "Sin conexión a internet",
                    Snackbar.LENGTH_LONG
            ).show()
        }
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
                        println("we have response length: ${response.length()}")
                        searchResults.clear()
                        while(i < response.length()){
                            println("here")
                            println("search result $i ${response[i]}")
                            //searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
                            i++
                        }
                        //customAdapter.notifyDataSetChanged()
                        loadingCircle.visibility = View.GONE
                        resultLayout.visibility = View.VISIBLE
                    }
                    override fun onError(error: ANError) {
                        println(error.message)
                        Snackbar.make(
                                requireView(),
                                "Error cargando inmuebles",
                                Snackbar.LENGTH_LONG
                        ).show()
                        loadingCircle.visibility = View.GONE
                    }
                })
    }
}


