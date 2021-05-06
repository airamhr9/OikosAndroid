package com.example.oikos.ui.home

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import objects.*
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
    lateinit var emptyLayout : LinearLayout
    lateinit var noSearchButon : AppCompatButton
    lateinit var user : Usuario

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        loadUser()
        return inflater.inflate(R.layout.fragment_home_searches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchResults = ArrayList()
        loadingCircle = view.findViewById(R.id.loading_searches)
        resultLayout = view.findViewById(R.id.results_searches)
        emptyLayout = view.findViewById(R.id.empty_layout)
        noSearchButon = view.findViewById(R.id.no_search_button)
        loadingCircle.visibility = View.VISIBLE
        emptyLayout.visibility = View.GONE
        resultLayout.visibility = View.GONE

        val resultsRecycler = view.findViewById<View>(R.id.results_searches_recycler) as RecyclerView
        adapter = SearchAdapter(searchResults, requireContext())
        resultsRecycler.adapter = adapter
        resultsRecycler.layoutManager = LinearLayoutManager(context)
        noSearchButon.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.navigation_search)
        }
        getResults()
    }

    private fun getResults(){
        if ((activity as MainActivity).isNetworkConnected()) {
            AndroidNetworking.get("http://10.0.2.2:9000/api/busqueda/")
                    .addQueryParameter("id", user.id.toString())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                            println(response.toString())
                            var i = 0
                            while(i < response.length()){
                                val busqueda = Busqueda.fromJson(JsonParser.parseString(response[i].toString()).asJsonObject)
                                searchResults.add(busqueda)
                                i++
                            }
                            adapter.notifyDataSetChanged()
                            if(response.length() == 0)
                                emptyLayout.visibility = View.VISIBLE
                            loadingCircle.visibility = View.GONE
                            resultLayout.visibility = View.VISIBLE
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
    private fun loadUser(){
        val sharedPref = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val savedUser = (sharedPref?.getString("saved_user", ""))
        val savedJsonUser: JsonObject = JsonParser.parseString(savedUser).asJsonObject
        user = Usuario.fromJson(savedJsonUser)
    }
}


