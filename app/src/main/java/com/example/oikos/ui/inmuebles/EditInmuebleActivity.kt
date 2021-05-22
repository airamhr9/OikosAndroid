package com.example.oikos.ui.inmuebles

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.bumptech.glide.Glide
import com.example.oikos.R
import com.google.android.material.snackbar.Snackbar
import objects.*
import java.net.URL


class EditInmuebleActivity : GestionInmuebleForm() {

    lateinit var inmuebleToEdit : InmuebleWithModelo
    var miniaturaUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inmuebleToEdit = intent.getSerializableExtra("inmueble") as InmuebleWithModelo
        tipoSpinner = findViewById(R.id.publicar_tipo)
        tipoSpinner.visibility = View.GONE

        val tipoTextLayoutParams = tipoInmuebleText.layoutParams
        tipoTextLayoutParams as ViewGroup.MarginLayoutParams
        tipoTextLayoutParams.marginEnd = 10
        tipoInmuebleText.layoutParams = tipoTextLayoutParams

        initializeData()
        findViewById<TextView>(R.id.publicar_toolbar_text).text = "Editar Inmueble"
        findViewById<AppCompatButton>(R.id.publicar_button).text = "Editar"
    }

    private fun initializeData() {
        val inmueble = inmuebleToEdit.inmueble

        tipoInmuebleText.text = inmuebleToEdit.modelo[0].toString().capitalize() + inmuebleToEdit.modelo.substring(1)
        if (inmueble.tipo == "Alquiler") {
            tipoBusqueda.check(R.id.alquiler_radio_button)
        } else {
            tipoBusqueda.check(R.id.compra_radio_button)
        }

        precioTextField.setText(inmueble.precio.toString())
        ciudadTextField.setText(inmueble.ciudad)
        direccionTextField.setText(inmueble.direccion)
        superficieTextField.setText(inmueble.superficie.toString())
        descripcionTextField.setText(inmueble.descripcion)
        for (imagen in inmueble.imagenes) {
            addImageFromUrl(imagen)
        }

        when(inmuebleToEdit.modelo) {
            "piso" -> {
                tipoSpinner.setSelection(pisoPos)
                currentType = pisoPos
            }
            "local" -> {
                tipoSpinner.setSelection(localPos)
                currentType = localPos
            }
            "garaje" -> {
                tipoSpinner.setSelection(garajePos)
                currentType = garajePos
            }
            "habitacion" -> {
                tipoSpinner.setSelection(habitacionPos)
                currentType = habitacionPos
            }
        }

        when (currentType) {
            pisoPos -> {
                inmueble as Piso
                numCompLayout.visibility = View.GONE
                bañosTextField.setText(inmueble.baños.toString())
                habitacionesTextField.setText(inmueble.habitaciones.toString())
                garajeCheckbox.isChecked = inmueble.garaje
            }
            habitacionPos -> {
                inmueble as Habitacion
                bañosTextField.setText(inmueble.baños.toString())
                habitacionesTextField.setText(inmueble.habitaciones.toString())
                garajeCheckbox.isChecked = inmueble.garaje
                numCompTextField.setText(inmueble.numCompañeros.toString())
            }
            localPos -> {
                inmueble as Local
                habsLayout.visibility = View.GONE
                numCompLayout.visibility = View.GONE
                garajeLayout.visibility = View.GONE
                bañosTextField.setText(inmueble.baños.toString())
            }
            garajePos -> {
                habsLayout.visibility = View.GONE
                bañosLayout.visibility = View.GONE
                garajeLayout.visibility = View.GONE
                numCompLayout.visibility = View.GONE
                inmueble as Garaje
            }
        }

        latitud = inmueble.latitud
        longitud = inmueble.longitud
        locationImage.visibility = View.VISIBLE
        locationText.text = "Añadida"

    }

    private fun addImageFromUrl(originalUrl: String) {
        val inflater: LayoutInflater = LayoutInflater.from(applicationContext)
        val newCard = inflater.inflate(R.layout.publicar_image_card, fotoLayout, false)
        val imageView = newCard.findViewById<ImageView>(R.id.image_inmueble)
        var newUrl = URL(originalUrl)
        newUrl = URL("http://10.0.2.2:9000${newUrl.path}")
        Glide.with(newCard).asBitmap().load(newUrl.toString()).into(imageView)
        fotoLayout.addView(newCard)
        newCard.findViewById<ImageButton>(R.id.remove_image).setOnClickListener {
            //fotoLayout.removeView(newCard)
            //inmuebleToEdit.inmueble.imagenes.remove(originalUrl)
            showMenuInEdited(newCard, originalUrl)
        }
    }

    private fun showMenuInEdited (card : View, url : String) {
        val popup = PopupMenu(applicationContext, card)
        popup.menuInflater.inflate(R.menu.image_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.set_main_image -> {
                    println ("Hi")
                    if (mainImage != null) {
                        mainImage!!.findViewById<ImageView>(R.id.main_image).visibility = View.GONE
                    }
                    mainImage = card
                    mainUri = null
                    miniaturaUrl = url
                    mainImage!!.findViewById<ImageView>(R.id.main_image).visibility = View.VISIBLE
                    true
                }
                R.id.delete -> {
                    fotoLayout.removeView(card)
                    inmuebleToEdit.inmueble.imagenes.remove(url)
                    true
                }
                else ->  super.onContextItemSelected(menuItem)
            }
        }
        popup.setOnDismissListener {
        }
        popup.show()
    }

    override fun numImages(): Int {
        return imageUris.size + inmuebleToEdit.inmueble.imagenes.size
    }

    override fun processImages(): ArrayList<String> {
        val images = super.processImages()
        images.addAll(inmuebleToEdit.inmueble.imagenes)
        miniaturaUrl?.let {
            images.remove(miniaturaUrl)
            images.add(0, miniaturaUrl!!)
        }
        return images
    }

    override fun sendToDB(inmueble: DatosInmueble, modelo: String) {
        val query = AndroidNetworking.put("http://10.0.2.2:9000/api/inmueble/")
        inmueble.id = inmuebleToEdit.inmueble.id
        query.addApplicationJsonBody(inmueble.toJson())
        query.addQueryParameter("modelo", modelo)
        query.setPriority(Priority.HIGH).build().getAsString(
            object : StringRequestListener {
                override fun onResponse(response: String) {
                    Snackbar.make(
                        window.decorView.rootView,
                        "Editado con éxito",
                        Snackbar.LENGTH_LONG
                    ).show()
                    intent.putExtra("inmueble", inmueble)
                    setResult(Activity.RESULT_OK, intent)
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