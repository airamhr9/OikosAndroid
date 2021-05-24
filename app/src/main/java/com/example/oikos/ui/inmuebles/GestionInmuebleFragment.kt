package com.example.oikos.ui.inmuebles

import android.app.Activity
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
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.example.oikos.LoadUserActivity
import com.example.oikos.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.DatosInmueble
import objects.InmuebleFactory
import objects.InmuebleWithModelo
import objects.Usuario
import org.json.JSONArray

class GestionInmuebleFragment : Fragment()  {
    val PUBLISH_ACTIVITY = 15
    val EDIT_ACTIVITY = 35
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var resultLayout : NestedScrollView
    lateinit var visibleLayout : RecyclerView
    lateinit var invisibleLayout : RecyclerView
    lateinit var visibleAdapter : GestionAdapter
    lateinit var invisibleAdapter : GestionAdapter
    lateinit var emptyLayout : LinearLayout
    lateinit var user : Usuario

    lateinit var visibleInmuebles : ArrayList<InmuebleWithModelo>
    lateinit var invisibleInmuebles : ArrayList<InmuebleWithModelo>
    lateinit var mementoVisibles : GestionAdapter.MementoImuebles
    lateinit var mementoInvisibles : GestionAdapter.MementoImuebles

    //val command : UndoCommand = UndoCommand(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_gestion_inmueble, container, false)
        val publishFab = root.findViewById<FloatingActionButton>(R.id.publish_fab)
        user = (activity as LoadUserActivity).loadUser()
        publishFab.setOnClickListener {
            val intent = Intent(requireContext(), PublicarAnunciosActivity::class.java)

            startActivityForResult(intent, PUBLISH_ACTIVITY)
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
            query.addQueryParameter("propietario", user.id.toString())
            emptyLayout.visibility = View.GONE
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
                            loadingCircle.visibility = View.GONE
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
        val completeList = ArrayList<InmuebleWithModelo>()
        var i = 0
        println("RESPONSE OF ${response.length()} ELEMENTS")
        while(i < response.length()){
            val modelo =  response.getJSONObject(i)["modelo"].toString()
            println("MODELO $modelo")
            val inmueble = InmuebleFactory().new(JsonParser.parseString(response[i].toString()).asJsonObject, modelo)
            completeList.add(InmuebleWithModelo(inmueble, modelo))
            i++
        }
        if(completeList.size == 0){
            emptyLayout.visibility = View.VISIBLE
            loadingCircle.visibility = View.GONE
            return
        } else {
            resultLayout.visibility = View.VISIBLE
        }
        loadingCircle.visibility = View.GONE

        visibleInmuebles.addAll(completeList.filter { it.inmueble.disponible } as ArrayList<InmuebleWithModelo>)
        invisibleInmuebles.addAll(completeList.filter { !it.inmueble.disponible } as ArrayList<InmuebleWithModelo>)
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

    fun updateInmueble(inmuebleWithModelo : InmuebleWithModelo, visible : Boolean){
        if(visible) {
            visibleInmuebles.remove(inmuebleWithModelo)
            inmuebleWithModelo.inmueble.disponible = false
            invisibleInmuebles.add(inmuebleWithModelo)
        } else {
            invisibleInmuebles.remove(inmuebleWithModelo)
            inmuebleWithModelo.inmueble.disponible = true
            visibleInmuebles.add(inmuebleWithModelo)
        }
        visibleAdapter.notifyDataSetChanged()
        invisibleAdapter.notifyDataSetChanged()
        updateInDatabase(inmuebleWithModelo)
    }

    private fun updateInDatabase(inmuebleWithModelo : InmuebleWithModelo) {
        val query = AndroidNetworking.put("http://10.0.2.2:9000/api/inmueble/")
        query.addApplicationJsonBody(inmuebleWithModelo.inmueble.toJson())
        query.addQueryParameter("modelo", inmuebleWithModelo.modelo)
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

    fun deleteInmueble(inmuebleWithModelo : InmuebleWithModelo, visible : Boolean){
        if(visible) {
            mementoVisibles = visibleAdapter.guardar() as GestionAdapter.MementoImuebles
            visibleInmuebles.remove(inmuebleWithModelo)
            visibleAdapter.notifyDataSetChanged()
        } else {
            mementoInvisibles = invisibleAdapter.guardar() as GestionAdapter.MementoImuebles
            invisibleInmuebles.remove(inmuebleWithModelo)
            invisibleAdapter.notifyDataSetChanged()
        }
        if(invisibleInmuebles.size == 0 && visibleInmuebles.size == 0) {
            resultLayout.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
        deleteInDatabase(inmuebleWithModelo)
    }

    private fun deleteInDatabase(inmuebleWithModelo: InmuebleWithModelo) {
        val query = AndroidNetworking.delete("http://10.0.2.2:9000/api/inmueble/")
        query.addQueryParameter("id", inmuebleWithModelo.inmueble.id.toString())
        query.setPriority(Priority.MEDIUM)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String) {
                        val snackbar = Snackbar.make(
                                requireView(),
                                "Inmueble eliminado con éxito",
                                Snackbar.LENGTH_LONG
                        )
                        snackbar.setAction("Deshacer") {
                            if(inmuebleWithModelo.inmueble.disponible)
                                mementoVisibles.restaurar()
                            else mementoInvisibles.restaurar()
                        }
                        snackbar.show()
                    }
                    override fun onError(error: ANError) {
                        Snackbar.make(
                                requireView(),
                                "Error eliminando inmueble",
                                Snackbar.LENGTH_LONG
                        ).show()
                    }
                })
    }

    fun startEditActivity(inmuebleWithModelo: InmuebleWithModelo) {
        if(inmuebleWithModelo.inmueble.disponible)
            mementoVisibles = visibleAdapter.guardar() as GestionAdapter.MementoImuebles
        else mementoInvisibles = invisibleAdapter.guardar() as GestionAdapter.MementoImuebles
        val intent = Intent(context, EditInmuebleActivity::class.java)
        intent.putExtra("inmueble", inmuebleWithModelo)
        startActivityForResult(intent, EDIT_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == PUBLISH_ACTIVITY) {
                Snackbar.make(
                        requireView(),
                        "Inmueble publicado con éxito",
                        Snackbar.LENGTH_LONG
                ).show()
            } else {
                val snackbar = Snackbar.make(
                        requireView(),
                        "Inmueble editado con éxito",
                        Snackbar.LENGTH_LONG
                )
                snackbar.setAction("Deshacer") {
                    val inmueble = data?.extras?.getSerializable("inmueble") as DatosInmueble
                    if(inmueble.disponible)
                        mementoVisibles.restaurar()
                    else mementoInvisibles.restaurar()
                }
                snackbar.show()
            }

            visibleInmuebles.clear()
            invisibleInmuebles.clear()
            getInmuebles()
        }
    }

    fun setState(visibleInmuebles : ArrayList<InmuebleWithModelo>,
                invisibleInmuebles : ArrayList<InmuebleWithModelo>,
                inmuebleModificado : InmuebleWithModelo) {
        if (inmuebleModificado.inmueble.disponible) {
            if (visibleInmuebles.none { it.inmueble.id == inmuebleModificado.inmueble.id }) {
                postInmueble(inmuebleModificado.inmueble, inmuebleModificado.modelo)
            } else {
                updateInDatabase(inmuebleModificado)
            }
            this.visibleInmuebles.clear()
            this.visibleInmuebles.addAll(visibleInmuebles)
            visibleAdapter.notifyDataSetChanged()
        } else {
            if (invisibleInmuebles.none { it.inmueble.id == inmuebleModificado.inmueble.id }) {
                postInmueble(inmuebleModificado.inmueble, inmuebleModificado.modelo)
            } else {
                updateInDatabase(inmuebleModificado)
            }
            this.invisibleInmuebles.clear()
            this.invisibleInmuebles.addAll(invisibleInmuebles)
            invisibleAdapter.notifyDataSetChanged()
        }
    }

    private fun postInmueble(inmueble: DatosInmueble, modelo : String){
        val query = AndroidNetworking.post("http://10.0.2.2:9000/api/inmueble/")
        query.addApplicationJsonBody(inmueble.toJson())
        query.addQueryParameter("modelo", modelo)
        query.setPriority(Priority.HIGH).build().getAsString(
                object : StringRequestListener {
                    override fun onResponse(response: String) {
                        Snackbar.make(
                                requireView(),
                                "Inmueble recuperado",
                                Snackbar.LENGTH_LONG
                        ).show()
                    }

                    override fun onError(error: ANError) {
                        Snackbar.make(
                                requireView(),
                                "Error al deshacer acción",
                                Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        )
    }

    }