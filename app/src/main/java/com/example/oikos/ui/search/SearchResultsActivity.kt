package com.example.oikos.ui.search

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.DatosInmueble
import org.json.JSONArray

class SearchResultsActivity : AppCompatActivity() {
    lateinit var searchResults: ArrayList<DatosInmueble>
    lateinit var customAdapter: CustomAdapter
    lateinit var resultLayout : NestedScrollView
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var emptyLayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        supportActionBar?.hide()

        searchResults = ArrayList()
        loadingCircle = findViewById(R.id.loading_search)
        resultLayout = findViewById(R.id.results)
        emptyLayout = findViewById(R.id.empty_layout)
        emptyLayout.visibility = View.GONE

        val filters : HashMap<String, String> = intent.extras!!.get("filters") as HashMap<String, String>
        getFilteredResults(filters)

        val resultsRecycler = findViewById<View>(R.id.results_recycler) as RecyclerView
        customAdapter = CustomAdapter(searchResults)
        resultsRecycler.adapter = customAdapter
        // Set layout manager to position the items
        resultsRecycler.layoutManager = LinearLayoutManager(this)
    }

    private fun getFilteredResults(filters : HashMap<String, String>){
        if (isNetworkConnected()) {
            val query = AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
            query.addQueryParameter("filtrada", "true")
            val filterKeys = filters.keys
            val jsonToSave = JsonObject()
            for (key in filterKeys) {
                query.addQueryParameter(key, filters[key])
                jsonToSave.addProperty(key, filters[key])
            }
            saveSearch(jsonToSave)
            resultLayout.visibility = View.GONE
            loadingCircle.visibility = View.VISIBLE
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
                        if(searchResults.size == 0){
                          emptyLayout.visibility = View.VISIBLE
                        } else resultLayout.visibility = View.VISIBLE
                    }
                    override fun onError(error: ANError) {
                        // handle error
                        println("ERROR: AAAAAAAAA " + error.message)
                        Toast.makeText(
                            applicationContext,
                            "Error cargando inmuebles",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        } else {
            Toast.makeText(
                applicationContext,
                "Sin conexión a internet",
                Toast.LENGTH_LONG
            ).show()
            loadingCircle.visibility = View.GONE
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun saveSearch(jsonObject: JsonObject){
        val sharedPrefs = this.getSharedPreferences("prefs", Context.MODE_PRIVATE) ?: return
        with(sharedPrefs.edit()){
            putString("saved_search", jsonObject.toString())
            apply()
            println("COMMITED")
        }
    }
}