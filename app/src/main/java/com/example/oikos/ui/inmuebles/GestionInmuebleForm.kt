package com.example.oikos.ui.inmuebles

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
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
import com.androidnetworking.interfaces.StringRequestListener
import com.example.oikos.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

abstract class GestionInmuebleForm : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val pisoPos = 0
    val habitacionPos = 1
    val garajePos = 2
    val localPos = 3
    val GetCoordsActivity = 8

    private val ResultLoadImage = 1
    lateinit var fotoLayout: FlexboxLayout
    lateinit var imageUris : ArrayList<Uri>

    lateinit var precioTextField : TextInputEditText
    lateinit var ciudadTextField : TextInputEditText
    lateinit var direccionTextField : TextInputEditText
    lateinit var superficieTextField : TextInputEditText
    lateinit var bañosTextField : TextInputEditText
    lateinit var habitacionesTextField : TextInputEditText
    lateinit var numCompTextField : TextInputEditText
    lateinit var descripcionTextField : TextInputEditText
    lateinit var garajeCheckbox : CheckBox
    lateinit var tipoBusqueda : RadioGroup
    lateinit var tipoInmuebleText : AppCompatTextView
    lateinit var locationImage : ImageView
    lateinit var locationText : TextView
    lateinit var locationButton : Button
    var latitud : Double? = null
    var longitud : Double? = null
    var currentType = pisoPos
    lateinit var user : Usuario

    lateinit var numCompLayout : LinearLayout
    lateinit var habsLayout : LinearLayout
    lateinit var bañosLayout : LinearLayout
    lateinit var garajeLayout : LinearLayout
    lateinit var tipoSpinner : AppCompatSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publicar_anuncios)
        loadUser()
        supportActionBar?.hide()

        setUpImageChooser()
        initializeFields()

        locationButton.setOnClickListener {
            val i = Intent(this, SelectCoordinatesActivity::class.java)
            startActivityForResult(i, GetCoordsActivity);
        }

        findViewById<AppCompatButton>(R.id.publicar_button).setOnClickListener {
            sendInmueble()
        }
    }

    fun setUpSpinner() {
        tipoSpinner = findViewById(R.id.publicar_tipo)
        ArrayAdapter.createFromResource(
                this,
                R.array.spinner_values,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSpinner.adapter = adapter
        }
        tipoSpinner.onItemSelectedListener = this
    }

    private fun initializeFields(){
        precioTextField = findViewById(R.id.publicar_precio)
        ciudadTextField = findViewById(R.id.publicar_ciudad)
        direccionTextField = findViewById(R.id.publicar_direccion)
        superficieTextField = findViewById(R.id.publicar_superficie)
        bañosTextField = findViewById(R.id.publicar_baños)
        numCompTextField = findViewById(R.id.publicar_numComp)
        habitacionesTextField = findViewById(R.id.publicar_habs)
        garajeCheckbox = findViewById(R.id.publicar_garaje)
        descripcionTextField = findViewById(R.id.publicar_desc)
        locationButton = findViewById(R.id.publicar_add_location)
        locationImage = findViewById(R.id.publicar_add_loc_image)
        locationText = findViewById(R.id.publicar_add_loc_text)
        locationImage.visibility = View.INVISIBLE
        locationText.text = "Sin añadir"

        bañosLayout = findViewById(R.id.baños_layout)
        habsLayout = findViewById(R.id.habitaciones_layout)
        numCompLayout = findViewById(R.id.numComp_layout)
        garajeLayout = findViewById(R.id.garaje_layout)

        tipoBusqueda = findViewById(R.id.tipo_busqueda_radio_group)
        tipoInmuebleText = findViewById(R.id.publicar_tipo_text)
    }

    private fun setUpImageChooser() {
        fotoLayout = findViewById(R.id.foto_layout)
        val fotoCard = findViewById<CardView>(R.id.foto_card)
        fotoCard.setOnClickListener {
            chooseImage()
        }
        imageUris = ArrayList()
    }

    private fun chooseImage(){
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Seleccione una imagen"), ResultLoadImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("ON RESULT")
        println("request code  $requestCode")
        if (resultCode == RESULT_OK && data != null) {
            if(requestCode == ResultLoadImage){
                val selectedImageUri : Uri? = data.data
                if(selectedImageUri != null){
                    val inflater: LayoutInflater = LayoutInflater.from(applicationContext)
                    val newCard = inflater.inflate(R.layout.publicar_image_card, fotoLayout, false)
                    newCard.findViewById<ImageView>(R.id.image_inmueble).setImageURI(
                            selectedImageUri
                    )
                    fotoLayout.addView(newCard)
                    imageUris.add(selectedImageUri)
                    newCard.findViewById<ImageButton>(R.id.remove_image).setOnClickListener {
                        fotoLayout.removeView(newCard)
                        imageUris.remove(selectedImageUri)
                    }
                }
            }
            else {
                println("COORDS RESULT")
                val coords = data.getSerializableExtra("coords") as GeoCoordsSerializable
                latitud = coords.latitud
                longitud = coords.longitud
                locationImage.visibility = View.VISIBLE
                locationText.text = "Añadida"
            }
        }
    }

    private fun sendInmueble() {
        val inmuebleToSend = getFormData()
        if(inmuebleToSend != null) {
            sendToDB(inmuebleToSend.inmueble, inmuebleToSend.modelo)
        }
    }

    abstract fun numImages() : Int

    open fun getFormData() : InmuebleWithModelo?{
        if(numImages() <= 0) {
            Snackbar.make(
                    window.decorView.rootView,
                    "Se necesita al menos una foto",
                    Snackbar.LENGTH_LONG
            ).show()
            fotoLayout.requestFocus()
            return null
        }

        val tipo = if (tipoBusqueda.checkedRadioButtonId == R.id.alquiler_radio_button)  "Alquiler" else "Venta"
        val modeloInm = tipoInmuebleText.text.toString().toLowerCase(Locale.ROOT)
        val modelo = if(modeloInm == "habitación") "habitacion" else modeloInm

        val precio = precioTextField.text.toString()
        if(precio == ""){
            precioTextField.error = "Precio es obligatorio"
            precioTextField.requestFocus()
            return null
        }
        val ciudad = ciudadTextField.text.toString()
        if(ciudad == ""){
            ciudadTextField.error = "Ciudad es obligatorio"
            ciudadTextField.requestFocus()
            return null
        }
        val direccion = direccionTextField.text.toString()
        if(direccion == ""){
            direccionTextField.error = "La dirección es obligatoria"
            direccionTextField.requestFocus()
            return null
        }
        if(latitud == null){
            Snackbar.make(
                    window.decorView.rootView,
                    "La localización es obligatoria",
                    Snackbar.LENGTH_LONG
            ).show()
            locationButton.requestFocus()
            return null
        }
        val superficie = superficieTextField.text.toString()
        if(superficie == ""){
            superficieTextField.error = "La superficie es obligatoria"
            superficieTextField.requestFocus()
            return null
        }
        val descripcion = descripcionTextField.text.toString()
        if(descripcion == ""){
            descripcionTextField.error = "La descripción es obligatoria"
            descripcionTextField.requestFocus()
            return null
        }

        lateinit var inmueble : DatosInmueble
        val inmuebleFactory = InmuebleFactory()

        when (currentType) {
            pisoPos -> {
                val baños = bañosTextField.text.toString()
                if (baños == "") {
                    bañosTextField.error = "Los baños son obligatorios"
                    bañosTextField.requestFocus()
                    return null
                }
                val habitaciones = habitacionesTextField.text.toString()
                if (habitaciones == "") {
                    habitacionesTextField.error = "Las habitaciones son obligatorias"
                    habitacionesTextField.requestFocus()
                    return null
                }
                val garaje = garajeCheckbox.isChecked
                inmueble = inmuebleFactory.new(
                        -1, true, tipo, superficie.toInt(), precio.toDouble(),
                        user, descripcion, direccion, ciudad, latitud!!, longitud!!, processImages(),
                        habitaciones.toInt(), baños.toInt(), garaje
                )
            }
            habitacionPos -> {
                val baños = bañosTextField.text.toString()
                if (baños == "") {
                    bañosTextField.error = "Los baños son obligatorios"
                    bañosTextField.requestFocus()
                    return null
                }
                val habitaciones = habitacionesTextField.text.toString()
                if (habitaciones == "") {
                    habitacionesTextField.error = "Las habitaciones son obligatorias"
                    habitacionesTextField.requestFocus()
                    return null
                }
                val garaje = garajeCheckbox.isChecked
                val numComp = numCompTextField.text.toString()
                if (numComp == "") {
                    numCompTextField.error = "El número de compañeros es obligatorio"
                    numCompTextField.requestFocus()
                    return null
                }
                inmueble = inmuebleFactory.new(
                        -1, true, tipo, superficie.toInt(), precio.toDouble(),
                        user, descripcion, direccion, ciudad, latitud!!, longitud!!, processImages(),
                        habitaciones.toInt(), baños.toInt(), garaje, numComp.toInt()
                )
            }
            localPos -> {
                val baños = bañosTextField.text.toString()
                if (baños == "") {
                    bañosTextField.error = "Los baños son obligatorios"
                    bañosTextField.requestFocus()
                    return null
                }
                inmueble = inmuebleFactory.new(
                        -1, true, tipo, superficie.toInt(), precio.toDouble(),
                        user, descripcion, direccion, ciudad, latitud!!, longitud!!, processImages(),
                        baños.toInt(),
                )
            }
            garajePos -> {
                inmueble = inmuebleFactory.new(
                        -1, true, tipo, superficie.toInt(), precio.toDouble(),
                        user, descripcion, direccion, ciudad, latitud!!, longitud!!, processImages(),
                )
            }
        }
        return InmuebleWithModelo(inmueble, modelo)
    }

    abstract fun sendToDB(inmueble: DatosInmueble, modelo : String)

    open fun processImages() : ArrayList<String> {
        return processUris(imageUris)
    }

    private fun processUris(uris: ArrayList<Uri>) : ArrayList<String> {
        val result = ArrayList<String>()
        for(uri in uris) {
            val query = AndroidNetworking.post("http://10.0.2.2:9000/api/image/")
            val file = getFile(applicationContext, uri)
            val name = UUID.randomUUID().toString() + "." + file.extension
            result.add(name)
            val newFile = File(filesDir, name)
            file.copyTo(newFile)
            query.addFileBody(newFile)
            query.addQueryParameter("name", name)
            query.setTag("Images")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(object : StringRequestListener {
                        override fun onResponse(response: String?) {
                            println("Imagen OK")
                            newFile.delete()
                        }

                        override fun onError(anError: ANError?) {
                            println("Error imagen")
                            newFile.delete()
                        }
                    })
        }
        return result
    }

    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                if (ins != null) {
                    createFileFromStream(ins, destinationFilename)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun onBackPressed (view : View) {
        super.onBackPressed()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedModel = parent?.getItemAtPosition(position) as String
        if(currentType != position) {
            currentType = position
            tipoInmuebleText.text = selectedModel
            resetModelFilters()
        }
        showModelFilters(position)
    }

    private fun resetModelFilters(){
        habsLayout.visibility = View.GONE
        habitacionesTextField.setText("")
        tipoBusqueda.getChildAt(0).isEnabled = true
        bañosLayout.visibility = View.GONE
        bañosTextField.setText("")
        garajeLayout.visibility = View.GONE
        garajeCheckbox.isChecked = false
        numCompLayout.visibility = View.GONE
        numCompTextField.setText("")
    }

    private fun showModelFilters(position: Int){
        when (position) {
            pisoPos -> {
                habsLayout.visibility = View.VISIBLE
                bañosLayout.visibility = View.VISIBLE
                garajeLayout.visibility = View.VISIBLE
            }
            habitacionPos -> {
                tipoBusqueda.check(R.id.alquiler_radio_button)
                tipoBusqueda.getChildAt(0).isEnabled = false
                bañosLayout.visibility = View.VISIBLE
                numCompLayout.visibility = View.VISIBLE
                garajeLayout.visibility = View.VISIBLE
                habsLayout.visibility = View.VISIBLE
            }
            localPos -> {
                bañosLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun loadUser(){
        val sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE)
        val savedUser = (sharedPref?.getString("saved_user", ""))
        val savedJsonUser: JsonObject = JsonParser.parseString(savedUser).asJsonObject
        user = Usuario.fromJson(savedJsonUser)
    }
}