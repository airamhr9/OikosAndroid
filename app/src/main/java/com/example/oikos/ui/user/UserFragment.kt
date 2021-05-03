package com.example.oikos.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.DatosInmueble
import objects.Preferencia
import org.json.JSONArray
import org.json.JSONObject
import android.content.Intent as Intent

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var datosFicha : DatosInmueble
    lateinit var loadingCircle : ContentLoadingProgressBar
    lateinit var editButton : AppCompatButton
    lateinit var resultLayout : LinearLayout
   // lateinit var  tCiudad : TextView
    lateinit var preference : Preferencia


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)

        //loadingCircle = root.findViewById(R.id.loading_search2)
        //resultLayout = root.findViewById(R.id.preference_layout)
        //loadingCircle.visibility = View.VISIBLE
        //resultLayout.visibility = View.GONE

        //editButton = root.findViewById(R.id.bEditar)
        //editButton.visibility = View.GONE

/*
        if ((activity as MainActivity).isNetworkConnected()) {
            AndroidNetworking.get("http://10.0.2.2:9000/api/preferencias/")
                    .addQueryParameter("id", "1")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            var jsonPreferences = JsonParser.parseString(response.toString()).asJsonObject
                            println("preferences: $jsonPreferences")
                            preference = Preferencia.fromJson(jsonPreferences)
                            loadingCircle.visibility = View.GONE
                            resultLayout.visibility = View.VISIBLE
                            editButton.visibility = View.VISIBLE
                            printFilters(preference)

                            editButton.setOnClickListener {
                                val menuPref = Intent(context, preferences :: class.java)
                                menuPref.putExtra("preferencias", preference)
                                startActivity(menuPref)
                            }
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(
                                    activity?.applicationContext,
                                    "Compruebe la conexión a internet",
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
*/
        return root
    }

    override fun onResume() {
        super.onResume()

/*
        if ((activity as MainActivity).isNetworkConnected()) {
            AndroidNetworking.get("http://10.0.2.2:9000/api/preferencias/")
                    .addQueryParameter("id", "1")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            var jsonPreferences = JsonParser.parseString(response.toString()).asJsonObject
                            println("preferences: $jsonPreferences")
                            preference = Preferencia.fromJson(jsonPreferences)
                            loadingCircle.visibility = View.GONE
                            resultLayout.visibility = View.VISIBLE
                            editButton.visibility = View.VISIBLE
                            printFilters(preference)

                            editButton.setOnClickListener {
                                val menuPref = Intent(context, preferences :: class.java)
                                menuPref.putExtra("preferencias", preference)
                                startActivity(menuPref)
                            }
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(
                                    activity?.applicationContext,
                                    "Compruebe la conexión a internet",
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
*/
    }

/*
    fun printFilters(preferences: Preferencia){
        val tCiudad = requireView().findViewById<TextView>(R.id.tCiudad)
        val tTipo = requireView().findViewById<TextView>(R.id.tTipo)
        val tPrecio =  requireView().findViewById<TextView>(R.id.tPrecio)
        val tHabs =  requireView().findViewById<TextView>(R.id.tHabs)
        val tBaño =  requireView().findViewById<TextView>(R.id.tBaño)
        val tSuperficie =  requireView().findViewById<TextView>(R.id.tSuperficie)
        val tGaraje =  requireView().findViewById<TextView>(R.id.tGaraje)

         tCiudad.text =  "${preferences.ciudad}"
         tTipo.text = "${preferences.tipo}"
         if(preferences.precio_max != Double.MAX_VALUE)
            tPrecio.text = "${preferences.precio_min} - ${preferences.precio_max}€"
         else {
             tPrecio.text = "${preferences.precio_min} - Sin límite"
         }
         if(preferences.habitaciones != null)
            tHabs.text = "${preferences.habitaciones}"
         if(preferences.baños != null)
            tBaño.text = "${preferences.baños}"
         tSuperficie.text = "${preferences.superficie_min} m² - ${preferences.superficie_max}m²"
         tGaraje.text = "${if(preferences.garaje) "Sí" else "No"}"

    }
*/

}