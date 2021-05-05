package com.example.oikos.ui.search

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.example.oikos.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.InmuebleFactory
import objects.InmuebleForList
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SearchResultsActivity : AppCompatActivity() {
    lateinit var searchResults: ArrayList<InmuebleForList>
    lateinit var customAdapter: CustomAdapter
    lateinit var resultLayout : NestedScrollView
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var emptyLayout : LinearLayout
    lateinit var filters : HashMap<String, String>
    lateinit var saveNameEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        supportActionBar?.hide()

        searchResults = ArrayList()
        loadingCircle = findViewById(R.id.loading_search)
        resultLayout = findViewById(R.id.results)
        emptyLayout = findViewById(R.id.empty_layout)
        emptyLayout.visibility = View.GONE

        filters = intent.extras!!.get("filters") as HashMap<String, String>
        getFilteredResults(filters)

        val resultsRecycler = findViewById<RecyclerView>(R.id.results_recycler)
        customAdapter = CustomAdapter(searchResults)
        resultsRecycler.adapter = customAdapter
        resultsRecycler.layoutManager = LinearLayoutManager(this)

        val saveBusquedaButton = findViewById<AppCompatImageButton>(R.id.save_busqueda_button)
        saveBusquedaButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.save_search_dialog, null)
            builder.setView(dialogView)
            saveNameEditText = dialogView.findViewById(R.id.search_name)
            builder.setTitle("Guardar búsqueda")
                    .setPositiveButton("Guardar"
                    ) { dialog, id ->
                        sendBusquedaToDB(saveNameEditText.text.toString())
                    }
                    .setNegativeButton("Cancelar"
                    ) { dialog, id ->
                        dialog.cancel()
                    }
            builder.show()
        }
    }

    private fun sendBusquedaToDB(name: String) {
        if (isNetworkConnected()) {
            val query = AndroidNetworking.post("http://10.0.2.2:9000/api/busqueda/")
            query.addQueryParameter("id", "1")
            val jsonToSave = JsonObject()
            val filterKeys = filters.keys
            for (key in filterKeys) {
                jsonToSave.addProperty(key, filters[key])
            }
            val completeJson = JsonObject()
            completeJson.addProperty("nombre", name)
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formatted = current.format(formatter)
            completeJson.addProperty("fecha", formatted)
            completeJson.add("busqueda", jsonToSave)
            query.addApplicationJsonBody(completeJson)
            query.setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(object : StringRequestListener {
                        override fun onResponse(response: String) {
                            Snackbar.make(
                                    window.decorView.rootView,
                                    "Búsqueda guardada correctamente",
                                    Snackbar.LENGTH_LONG
                            ).show()
                        }

                        override fun onError(error: ANError) {
                            Snackbar.make(
                                    window.decorView.rootView,
                                    "Error guardando búsqueda",
                                    Snackbar.LENGTH_LONG
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

    private fun getFilteredResults(filters: HashMap<String, String>){
        if (isNetworkConnected()) {
            val query = AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
            query.addQueryParameter("filtrada", "true")
            val filterKeys = filters.keys
            for (key in filterKeys) {
                query.addQueryParameter(key, filters[key])
            }
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
                        println("MODELO ES " + filters["modelo"])
                        processResponse(response, filters["modelo"]!!)
                    }

                    override fun onError(error: ANError) {
                        Snackbar.make(
                                window.decorView.rootView,
                                "Error cargando inmuebles",
                                Snackbar.LENGTH_LONG
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

    private fun processResponse(response: JSONArray, modelo: String){
        var i = 0
        println("we have response")
        while(i < response.length()){
            val inmueble = InmuebleFactory().new(JsonParser.parseString(response[i].toString()).asJsonObject, modelo)
            searchResults.add(InmuebleForList(inmueble, modelo))
            println("MODELO AGAIN IS " + modelo)
            i++
        }
        customAdapter.notifyDataSetChanged()
        loadingCircle.visibility = View.GONE
        if(searchResults.size == 0){
            emptyLayout.visibility = View.VISIBLE
        } else resultLayout.visibility = View.VISIBLE
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

    fun onBackPressed(view: View) {
        super.onBackPressed()
    }
}