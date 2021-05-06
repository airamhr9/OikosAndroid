package com.example.oikos.ui.user

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.OkHttpResponseListener
import com.androidnetworking.interfaces.StringRequestListener
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import objects.GeoCoordsSerializable
import objects.Usuario
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.PasswordAuthentication
import java.util.*
import kotlin.collections.ArrayList

class registro : AppCompatActivity() {

    private val ResultLoadImage = 1
lateinit var nombre : String
lateinit var  correo : String
lateinit var contraseña : String
lateinit var user : Usuario
    var regName : Boolean = false
    var regEmail : Boolean = false
    var regPass : Boolean = false
lateinit var  fotoLayout : ImageView
lateinit var eName : TextView
lateinit var eEmail:TextView
lateinit var ePass : TextView
lateinit var imageUris : Uri
var changedPhoto : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        setUpImageChooser()
        supportActionBar?.hide()


         eName = findViewById<TextView>(R.id.name)
        eEmail = findViewById<TextView>(R.id.email)
        ePass = findViewById<TextView>(R.id.password1)
        val ePass2 = findViewById<TextView>(R.id.password2)
        val badPass = findViewById<TextView>(R.id.tBadPass)
        val bRegistro = findViewById<Button>(R.id.bRegistrarse)
        val badEmail = findViewById<TextView>(R.id.badEmail)
        val badName = findViewById<TextView>(R.id.badName)

        bRegistro.setOnClickListener{
            postUser()
        }

        ePass2.addTextChangedListener {
            val newText = it.toString()
            contraseña = ePass.text.toString()
            if(contraseña == ePass2.text.toString() && contraseña != ""){
                badPass.visibility = View.GONE

                regPass = true
                if(regPass && regEmail && regName)  bRegistro.setEnabled(true)
                else bRegistro.setEnabled(false)


            } else{


                badPass.visibility = View.VISIBLE

                regPass = false
                bRegistro.setEnabled(false)
            }
        }

        ePass.addTextChangedListener {
            val newText = it.toString()
            contraseña = ePass.text.toString()
            if(contraseña == ePass2.text.toString() && contraseña != ""){
                badPass.visibility = View.GONE

                regPass = true
                if(regPass && regEmail && regName)  bRegistro.setEnabled(true)
                else bRegistro.setEnabled(false)
            } else{
                badPass.visibility = View.VISIBLE

                regPass = false
                bRegistro.setEnabled(false)
            }
        }

        eName.addTextChangedListener {
            if(eName.text.toString() == "") {
                badName.visibility = View.VISIBLE
                bRegistro.setEnabled(false)
                regName = false
            }
            else {
                badName.visibility = View.INVISIBLE
                regName = true
                if(regPass && regEmail && regName)  bRegistro.setEnabled(true)
                else bRegistro.setEnabled(false)
            }

        }

        eEmail.addTextChangedListener {
            var mail = eEmail.text.toString()

            println(isValidString(mail))
            if(mail != "" && isValidString(mail)) {
                badEmail.visibility = View.GONE
                regEmail = true
                if(regPass && regEmail && regName)  bRegistro.setEnabled(true)
                else bRegistro.setEnabled(false)
            }
            else {
                badEmail.visibility = View.VISIBLE
                bRegistro.setEnabled(false)
                regEmail = false
            }
        }


    }

    fun isValidString(str: String): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }

    private fun chooseImage(){
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Seleccione una imagen"), ResultLoadImage)
        changedPhoto = true
    }

  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ResultLoadImage && resultCode == RESULT_OK && data != null){
            val selectedImageUri : Uri? = data.data
            if(selectedImageUri != null){
                findViewById<ImageView>(R.id.avatar).setImageURI(selectedImageUri)
                }
            }
        }
    */

    private fun setUpImageChooser() {
        fotoLayout = findViewById<ImageView>(R.id.avatar)
        val bfoto = findViewById<Button>(R.id.bSel_imagen)
        fotoLayout.setBackgroundResource(R.drawable.default_user)

        bfoto.setOnClickListener {
            chooseImage()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("ON RESULT")
        println("request code  $requestCode")
        if (resultCode == RESULT_OK && data != null) {
            if(requestCode == ResultLoadImage){
                val selectedImageUri : Uri? = data.data
                if(selectedImageUri != null){
                   // val inflater: LayoutInflater = LayoutInflater.from(applicationContext)
                    //val newCard = inflater.inflate(R.layout.publicar_image_card, fotoLayout, false)
                    //newCard.findViewById<ImageView>(R.id.image_inmueble)
                            fotoLayout.setImageURI(
                            selectedImageUri
                    )
                    //fotoLayout.addView(newCard)
                    imageUris = selectedImageUri
                    println(imageUris)
                }
            }

        }
    }

    open fun processImages() : String {
        return processUris(imageUris)
    }

    private fun processUris(uris: Uri) : String {
        var result = ""
            val query = AndroidNetworking.post("http://10.0.2.2:9000/api/image/")
            val file = getFile(applicationContext, uris)
            val name = UUID.randomUUID().toString() + "." + file.extension
            result = name
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

        return result
    }

    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename: File = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
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


    private fun postUser(){
        val user = obtenerUsuario()
        val jsonUser = user.toJson()
        val query = AndroidNetworking.post("http://10.0.2.2:9000/api/user/")
        query.addApplicationJsonBody(jsonUser)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(object: StringRequestListener {
                    override fun onResponse(response: String) {
                        AlertDialog.Builder(this@registro)
                                .setTitle("Se ha registrado correctamente")
                                //.setMessage("")
                                .setPositiveButton("Ok"
                                ) { _, _ ->  val intent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)}
                                .show()
                        saveUser(jsonUser)
                       // finish()
                    }
                    override fun onError(anError: ANError?) {
                        AlertDialog.Builder(this@registro)
                                .setTitle("Ha ocurrido un error")
                                .setMessage("Compruebe su conexión o si se encuentra ya registrado")
                                .setPositiveButton("Ok"
                                ) { _, _ ->}
                                .show()
                    }
                })
    }



    fun obtenerUsuario() : Usuario{
        var myImage : String
        nombre = eName.text.toString()
        contraseña = ePass.text.toString()
        correo = eEmail.text.toString()

        if(changedPhoto) myImage = processImages()
        else myImage = "default_user.png"

        var myUser = Usuario(-1, nombre,correo, contraseña, myImage)
        println(correo)
        return myUser
    }

    private fun saveUser(jsonObject: JsonObject){
        val sharedPrefs = this@registro.getSharedPreferences("user", Context.MODE_PRIVATE) ?: return
        with(sharedPrefs.edit()){
            putString("saved_user", jsonObject.toString())
            apply()
            println("COMMITED")
        }
    }


}