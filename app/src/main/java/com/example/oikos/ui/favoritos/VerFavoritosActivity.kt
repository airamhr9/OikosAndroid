package com.example.oikos.ui.favoritos

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.oikos.LoadUserActivity
import com.example.oikos.R
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.InmuebleFactory
import objects.InmuebleModeloFav
import org.json.JSONArray

class VerFavoritosActivity : LoadUserActivity() {

    lateinit var searchResults: ArrayList<InmuebleModeloFav>
    lateinit var favAdapter: FavAdapter
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var  emptyLayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_favoritos)

        supportActionBar?.hide()
        findViewById<TextView>(R.id.publicar_toolbar_text).text = "Favoritos"

        emptyLayout = findViewById(R.id.empty_layout)
        loadingCircle = findViewById(R.id.loading_search3)
        getFilteredResults()

        searchResults = ArrayList()
        val favRecycler = findViewById<RecyclerView>(R.id.listaFavoritos)
        favAdapter = FavAdapter(searchResults, this)
        favRecycler.adapter = favAdapter
        favRecycler.layoutManager = LinearLayoutManager(this)
    }


    private fun getFilteredResults(){
        if (isNetworkConnected()) {
            val query = AndroidNetworking.get("http://10.0.2.2:9000/api/favorito/")
            query.addQueryParameter("usuario", getUserId().toString())
            // resultLayout.visibility = View.GONE
            loadingCircle.visibility = View.VISIBLE
            query.setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                           // println("MODELO ES " + filters["modelo"])
                            processResponse(response)
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

    private fun processResponse(response: JSONArray){
        var i = 0
        println("we have response")
        while(i < response.length()){
            println(response.getJSONObject(i))

            val modelo =  response.getJSONObject(i).getJSONObject("inmueble")["modelo"].toString()
           //val favorito =  response.getJSONObject(i)["favorito"].toString().toBoolean()
            val inmueble = InmuebleFactory().new(JsonParser.parseString(response.getJSONObject(i).getJSONObject("inmueble").toString()).asJsonObject, modelo)
            val nota =  response.getJSONObject(i)["notas"].toString()
            searchResults.add(InmuebleModeloFav(inmueble, modelo, true, nota))

            println("MODELO AGAIN IS " + modelo)
            i++
        }
        favAdapter.notifyDataSetChanged()
        loadingCircle.visibility = View.GONE
       if(searchResults.size == 0){
            emptyLayout.visibility = View.VISIBLE
       } //else resultLayout.visibility = View.VISIBLE
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun getUserId() : Int{
        val sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE)
        val savedUser = (sharedPref?.getString("saved_user", ""))
        val savedJsonUser: JsonObject = JsonParser.parseString(savedUser).asJsonObject
        return savedJsonUser["id"].asInt
    }

    fun onBackPressed(view: View) {
        super.onBackPressed()
    }

    fun eliminarFavorito(pos : Int) {
        searchResults.removeAt(pos)
        favAdapter.notifyDataSetChanged()
    }
}