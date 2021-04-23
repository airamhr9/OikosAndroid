package com.example.oikos.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.oikos.R
import com.example.oikos.ui.search.CustomAdapter
import com.example.oikos.ui.user.UserViewModel
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.DatosInmueble
import org.json.JSONArray


class SavedSearchFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var sharedPref : SharedPreferences
    lateinit var searchResults: ArrayList<DatosInmueble>
    lateinit var customAdapter: CustomAdapter
    lateinit var resultLayout : NestedScrollView
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var noSearchButon : AppCompatButton
    lateinit var emptyLayout : LinearLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_saved_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = view.context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        emptyLayout = view.findViewById(R.id.empty_layout)
        noSearchButon = view.findViewById(R.id.no_search_button)
        emptyLayout.visibility = View.INVISIBLE

        searchResults = ArrayList()
        loadingCircle = view.findViewById(R.id.loading_search_saved)
        resultLayout = view.findViewById(R.id.results_saved)
        loadingCircle.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE

        val resultsRecycler = view.findViewById<View>(R.id.results_saved_recycler) as RecyclerView
        //customAdapter = CustomAdapter(searchResults)
        //resultsRecycler.adapter = customAdapter
        resultsRecycler.layoutManager = LinearLayoutManager(context)

        val savedSearch = (sharedPref?.getString("saved_search", ""))
        println("SAVED SEARCH: $savedSearch")
        if(savedSearch == "" || savedSearch == null){
            loadingCircle.visibility = View.INVISIBLE
            emptyLayout.visibility = View.VISIBLE
            noSearchButon.setOnClickListener {
                val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                navController.navigate(R.id.navigation_search)
            }
            return
        }
        var savedJsonSearch : JsonObject = JsonParser.parseString(savedSearch).asJsonObject
        getSavedResults(savedJsonSearch)
    }

    fun getSavedResults(preferences: JsonObject){
        val query = AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
        query.addQueryParameter("filtrada", "true")
        val keySet = preferences.keySet()
        var filterString = ""
        for(key in keySet){
            filterString += ("$key: ${preferences.get(key).asString}\n")
            query.addQueryParameter(key, preferences.get(key).asString)
        }
        resultLayout.visibility = View.GONE
        loadingCircle.visibility = View.VISIBLE
        query.setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        var i = 0
                        searchResults.clear()
                        while (i < response.length()) {
                            println("here")
                            println("search result $i ${response[i]}")
                            //searchResults.add(DatosInmueble.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject))
                            i++
                        }
                        customAdapter.notifyDataSetChanged()
                        loadingCircle.visibility = View.GONE
                        resultLayout.visibility = View.VISIBLE
                        printFilters(preferences)
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

    fun printFilters(preferences: JsonObject){
        val filterTextView = requireView().findViewById<TextView>(R.id.filter_text_view)
        var filterString = ""
        for (key in preferences.keySet()) {
            when (key) {
                "ciudad" -> filterString += "Ciudad: ${preferences[key].asString}\n"
                "tipo" -> filterString += "Tipo: ${preferences[key].asString}\n"
                "precioMin" -> filterString += "Precio mínimo: ${preferences[key].asString}€\n"
                "precioMax" -> filterString += "Precio máximo: ${preferences[key].asString}€\n"
                "habitaciones" -> filterString += "Habitaciones: ${preferences[key].asString}\n"
                "baños" -> filterString += "Baños: ${preferences[key].asString}\n"
                "supMin" -> filterString += "Superficie mínima: ${preferences[key].asString}m²\n"
                "supMax" -> filterString += "Superficie máxima: ${preferences[key].asString}m²\n"
                "garaje" -> filterString += "Garaje: ${if(preferences[key].asBoolean) "Sí" else "No"}\n"
            }
        }
        filterTextView.text = filterString
    }

}
