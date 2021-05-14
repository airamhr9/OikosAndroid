package com.example.oikos.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.oikos.LoadUserActivity
import com.example.oikos.R
import com.example.oikos.ui.favoritos.FavAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.InmuebleFactory
import objects.InmuebleModeloFav
import org.json.JSONArray

class FavoritosFragment : Fragment() {

    lateinit var searchResults: ArrayList<InmuebleModeloFav>
    lateinit var favAdapter: FavoritosAdapter
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var  emptyLayout : LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_favoritos, container, false)
        emptyLayout = root.findViewById(R.id.empty_layout)
        loadingCircle = root.findViewById(R.id.loading_search3)
        getFilteredResults()

        searchResults = ArrayList()
        val favRecycler = root.findViewById<RecyclerView>(R.id.listaFavoritos)
        favAdapter = FavoritosAdapter(searchResults, this)
        favRecycler.adapter = favAdapter
        favRecycler.layoutManager = LinearLayoutManager(requireActivity())

        root.findViewById<AppCompatButton>(R.id.no_search_button).setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.navigation_search)
        }
        return root
    }

    private fun getFilteredResults(){
        if (isNetworkConnected()) {
            val query = AndroidNetworking.get("http://10.0.2.2:9000/api/favorito/")
            query.addQueryParameter("usuario", (activity as LoadUserActivity).loadUser().id.toString())
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
                                    requireView(),
                                    "Error cargando inmuebles",
                                    Snackbar.LENGTH_LONG
                            ).show()
                        }
                    })
        } else {
            Toast.makeText(
                    requireContext(),
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
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun eliminarFavorito(pos : Int) {
        searchResults.removeAt(pos)
        favAdapter.notifyDataSetChanged()
    }
}