package com.example.oikos.ui.user.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.oikos.R
import com.example.oikos.ui.user.data.LoginRepository
import com.example.oikos.ui.user.data.Result
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import objects.Usuario
import org.json.JSONObject
import java.security.AccessController.getContext

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
//    lateinit var usuario: Usuario
/*    fun login(username: String, password: String) {
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

                        }

                        override fun onError(error: ANError) {
                           println("Error en la peticion al server get User")
                        }
                    })


        }*/




        // can be launched in a separate asynchronous job
     /*   val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }*/


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 0
    }
}