package com.example.oikos.ui.inmuebles

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.*
import androidx.cardview.widget.CardView
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
import com.example.oikos.ui.search.CustomAdapter
import com.example.oikos.ui.search.SearchResultsActivity
import com.example.oikos.ui.search.localized.LocalizedSearch
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonParser
import objects.InmuebleFactory
import objects.InmuebleForList
import org.json.JSONArray
import org.w3c.dom.Text

class GestionInmuebleFragment : Fragment() {

    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var resultLayout : NestedScrollView
    lateinit var visibleLayout : RecyclerView
    lateinit var invisibleLayout : RecyclerView
    lateinit var visibleAdapter : GestionAdapter
    lateinit var invisibleAdapter : GestionAdapter
    lateinit var emptyLayout : LinearLayout

    lateinit var visibleInmuebles : ArrayList<InmuebleForList>
    lateinit var invisibleInmuebles : ArrayList<InmuebleForList>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_gestion_inmueble, container, false)

        val publishFab = root.findViewById<FloatingActionButton>(R.id.publish_fab)
        publishFab.setOnClickListener {
            val intent = Intent(requireContext(), PublicarAnunciosActivity::class.java)
            startActivity(intent)
        }
        loadingCircle = root.findViewById(R.id.loading_search)
        resultLayout = root.findViewById(R.id.results)
        emptyLayout = root.findViewById(R.id.empty_layout)
        emptyLayout.visibility = View.GONE
        resultLayout.visibility = View.GONE
        loadingCircle.visibility = View.VISIBLE

        visibleLayout = root.findViewById(R.id.inmuebles_visibles)
        invisibleLayout = root.findViewById(R.id.inmuebles_invisibles)

        visibleInmuebles = ArrayList()
        invisibleInmuebles = ArrayList()

        getInmuebles()

        visibleAdapter = GestionAdapter(visibleInmuebles, true, this)
        visibleLayout.adapter = visibleAdapter
        visibleLayout.layoutManager = LinearLayoutManager(requireContext())

        invisibleAdapter = GestionAdapter(invisibleInmuebles, false, this)
        invisibleLayout.adapter = invisibleAdapter
        invisibleLayout.layoutManager = LinearLayoutManager(requireContext())


        return root
    }
    private fun getInmuebles() {
        if (isNetworkConnected()) {
            val query = AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
            //TODO SUSTITUIR POR ID UNA VEZ HAYA LOGIN
            query.addQueryParameter("propietario", "1")
            resultLayout.visibility = View.GONE
            loadingCircle.visibility = View.VISIBLE
            query.setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
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
            Snackbar.make(
                    requireView(),
                    "Sin conexión a internet",
                    Snackbar.LENGTH_LONG
            ).show()
            loadingCircle.visibility = View.GONE
        }
    }

    private fun processResponse(response : JSONArray) {
        val completeList = ArrayList<InmuebleForList>()
        var i = 0
        println("RESPONSE OF ${response.length()} ELEMENTS")
        while(i < response.length()){
            val modelo =  response.getJSONObject(i)["modelo"].toString()
            println("MODELO $modelo")
            val inmueble = InmuebleFactory().new(JsonParser.parseString(response[i].toString()).asJsonObject, modelo)
            completeList.add(InmuebleForList(inmueble, modelo))
            i++
        }
        if(completeList.size == 0){
            emptyLayout.visibility = View.VISIBLE
            return
        } else {
            resultLayout.visibility = View.VISIBLE
        }
        loadingCircle.visibility = View.GONE

        visibleInmuebles.addAll(completeList.filter { it.inmueble.disponible } as ArrayList<InmuebleForList>)
        invisibleInmuebles.addAll(completeList.filter { !it.inmueble.disponible } as ArrayList<InmuebleForList>)
        visibleAdapter.notifyDataSetChanged()
        invisibleAdapter.notifyDataSetChanged()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun updateInmueble(inmuebleForList : InmuebleForList ,  visible : Boolean){
        if(visible) {
            visibleInmuebles.remove(inmuebleForList)
            inmuebleForList.inmueble.disponible = false
            invisibleInmuebles.add(inmuebleForList)
        } else {
            invisibleInmuebles.remove(inmuebleForList)
            inmuebleForList.inmueble.disponible = true
            visibleInmuebles.add(inmuebleForList)
        }
        visibleAdapter.notifyDataSetChanged()
        invisibleAdapter.notifyDataSetChanged()
        updateInDatabase(inmuebleForList)
    }
    private fun updateInDatabase(inmuebleForList : InmuebleForList) {
        val query = AndroidNetworking.put("http://10.0.2.2:9000/api/inmueble/")
        query.addApplicationJsonBody(inmuebleForList.inmueble.toJson())
        query.addQueryParameter("modelo", inmuebleForList.modelo)
        query.setPriority(Priority.MEDIUM)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String) {
                        Snackbar.make(
                                requireView(),
                                "Inmueble actualizado con éxito",
                                Snackbar.LENGTH_LONG
                        ).show()
                    }
                    override fun onError(error: ANError) {
                        Snackbar.make(
                                requireView(),
                                "Error actualizando inmueble",
                                Snackbar.LENGTH_LONG
                        ).show()
                    }
                })
    }

}