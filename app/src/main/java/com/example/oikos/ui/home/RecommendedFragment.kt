package com.example.oikos.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmuebleActivity
import com.example.oikos.ui.user.UserViewModel
import com.google.gson.JsonParser
import objects.DatosInmueble
import org.json.JSONObject

class RecommendedFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var datosFicha : DatosInmueble

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_recommended, container, false)
    }

    //TODO(Borrar este método cuando se haya acabado la ficha)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.ficha_button)

        //TODO(mover a tarjeta de inmueble)
        button.setOnClickListener {
            val intent = Intent(this.context, FichaInmuebleActivity::class.java)
            intent.putExtra("inmueble", datosFicha)
            startActivity(intent)
        }

        if ((activity as MainActivity).isNetworkConnected()) {
            AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
                    .addQueryParameter("id", "1")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            // do anything with response
                            datosFicha = DatosInmueble.fromJson(JsonParser.parseString(response.toString()).asJsonObject)
                        }

                        override fun onError(error: ANError) {
                            // handle error
                            val responseText : TextView = view.findViewById(R.id.response_text)
                            responseText.text = "Error joe"
                        }
                    })
        } else {
            Toast.makeText(
                    activity?.applicationContext,
                    "No internet connection",
                    Toast.LENGTH_LONG
            ).show()
        }

    }

    fun setData(view: View, text: String){
        val responseText : TextView = view.findViewById(R.id.response_text)
        responseText.text = text
    }



}


