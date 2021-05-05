package objects

import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.HashMap

data class Busqueda (var nombre : String, var fecha : String, var filters : JsonObject) {
    companion object {
        fun fromJson(jsonObject: JsonObject) : Busqueda {
            return Busqueda(jsonObject["nombre"].asString, jsonObject.get("fecha").asString, jsonObject.get("busqueda").asJsonObject)
        }
    }
}