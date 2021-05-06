package com.example.oikos.ui.user.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.oikos.MainActivity
import com.example.oikos.R
import com.example.oikos.ui.inmuebles.PublicarAnunciosActivity
import com.example.oikos.ui.user.registro
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.Usuario
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loading : ProgressBar
    lateinit var usuario: Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        supportActionBar?.hide()
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        loading = findViewById<ProgressBar>(R.id.loading)

        val bRegistrar = findViewById<Button>(R.id.bRegistrarse)
        bRegistrar.setOnClickListener {
            val intent = Intent(applicationContext, registro::class.java)
            startActivity(intent)
        }


        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        login(
                                username.text.toString(),
                                password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                login(username.text.toString(), password.text.toString())
            }
        }
    }


    fun login(username: String, password: String) {
        usuario = Usuario(-1,"","","","")
        AndroidNetworking.get("http://10.0.2.2:9000/api/user/")
                .addQueryParameter("mail", username)
                .addQueryParameter("contraseña", password)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        println("Peticion realizada")
                        val jsonUser = JsonParser.parseString(response.toString()).asJsonObject
                        saveUser(jsonUser)
                        usuario = Usuario.fromJson(jsonUser)
                        println(usuario.mail)
                        println(usuario.contraseña)
                        AlertDialog.Builder(this@LoginActivity)
                                .setTitle("Se ha conectado correctamente")
                                .setMessage("Ya puede empezar a usar Trobify")
                                .setPositiveButton("Ok"
                                ) { _, _ ->val intent = Intent(applicationContext, MainActivity::class.java)

                                    startActivity(intent)}
                                .show()
                    }

                    override fun onError(error: ANError) {
                        println("Error en la peticion al server get User")
                        AlertDialog.Builder(this@LoginActivity)
                                .setTitle("Usuario o contraseña incorrecta")
                                .setMessage("Inténtelo de nuevo o regístrese")
                                .setPositiveButton("Ok"
                                ) { _, _ ->}
                                .show()
                        loading.visibility = View.GONE
                    }
                })


    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
        ).show()
    }

    private fun saveUser(jsonObject: JsonObject){
        val sharedPrefs = this@LoginActivity.getSharedPreferences("user", Context.MODE_PRIVATE) ?: return
        with(sharedPrefs.edit()){
            putString("saved_user", jsonObject.toString())
            apply()
            println("COMMITED")
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}