package com.example.oikos.serverConnection

import android.view.View
import com.example.oikos.ui.home.RecommendedFragment
import com.google.gson.JsonParser
import objects.DatosInmueble
import okhttp3.*
import java.io.IOException


class ServerConnection {
    val client = OkHttpClient()
        fun printRequest(url: String) {
            println("ENTERED REQUEST")
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("REQUEST FAILURE")
                }

                override fun onResponse(call: Call, response: Response) = println(
                    response.body()?.string()
                )
            })
        }

        fun getInmuebleById(id: Int) : DatosInmueble {
            println("ENTERED REQUEST")
            val url = "http://10.0.2.2:9000/api/inmueble/?id=$id"
            val request = Request.Builder()
                .url(url)
                .build()

            lateinit var datosInmueble : DatosInmueble
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("REQUEST FAILURE")
                    return
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson =
                        JsonParser.parseString(response.body()?.string()).asJsonObject
                    datosInmueble = DatosInmueble.fromJson(responseJson)
                    return
                }
            })
            return datosInmueble
        }


}