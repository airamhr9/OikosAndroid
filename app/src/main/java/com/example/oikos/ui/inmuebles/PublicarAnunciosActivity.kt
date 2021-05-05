package com.example.oikos.ui.inmuebles

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.example.oikos.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import objects.DatosInmueble
import objects.GeoCoordsSerializable
import objects.InmuebleFactory
import objects.Usuario
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.collections.ArrayList


class PublicarAnunciosActivity :  GestionInmuebleForm() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentType = 1
        tipoBusqueda.check(R.id.alquiler_radio_button)
        super.setUpSpinner()
    }

    override fun numImages(): Int {
        return imageUris.size
    }

    override fun sendToDB(inmueble: DatosInmueble, modelo : String){
        val query = AndroidNetworking.post("http://10.0.2.2:9000/api/inmueble/")
        query.addApplicationJsonBody(inmueble.toJson())
        query.addQueryParameter("modelo", modelo)
        query.setPriority(Priority.HIGH).build().getAsString(
                object : StringRequestListener {
                    override fun onResponse(response: String) {
                        Snackbar.make(
                                window.decorView.rootView,
                                "Creado con éxito",
                                Snackbar.LENGTH_LONG
                        ).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    override fun onError(error: ANError) {
                        Snackbar.make(
                                window.decorView.rootView,
                                "Error al crear inmueble",
                                Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        )
    }
}