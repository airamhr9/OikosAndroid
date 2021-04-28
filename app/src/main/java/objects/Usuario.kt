package objects

import com.google.gson.JsonObject
import java.io.Serializable
import java.util.*

class Usuario(
    var nombre: String,
    var mail: String,
) : Serializable{
    var id : Int = 1

    fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("nombre", this.nombre)
        result.addProperty("mail", this.mail)
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Usuario {
            val nombre = jsonObject.get("nombre").asString.toString()
            val mail = jsonObject.get("mail").asString.toString()
            val usuario = Usuario(nombre, mail)
            usuario.id = jsonObject.get("id").asInt
            return Usuario(nombre, mail)
        }
    }

}