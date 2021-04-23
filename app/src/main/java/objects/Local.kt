package objects

import com.google.gson.JsonObject
import java.io.Serializable

class Local(id: Int,
            disponible: Boolean,
            tipo: String,
            superficie: Int,
            precio: Double,
            propietario: Usuario,
            descripcion: String,
            direccion: String,
            ciudad: String,
            latitud: Double,
            longitud: Double,
            imagenes: ArrayList<String>,
            var baños: Int,
) : Serializable, DatosInmueble(id, disponible, tipo, superficie, precio, propietario, descripcion,
        direccion, ciudad, latitud, longitud, imagenes) {

    companion object {
        fun fromJson(jsonObject: JsonObject): Local {
            val id = jsonObject.get("id").asInt
            val disponible = jsonObject.get("disponible").asBoolean
            val tipo = jsonObject.get("tipo").asString
            val superficie = jsonObject.get("superficie").asInt
            val precio = jsonObject.get("precio").asDouble
            val propietario = Usuario.fromJson(jsonObject.getAsJsonObject("propietario"))
            val descripcion = jsonObject.get("descripcion").asString.toString()
            val direccion = jsonObject.get("direccion").asString
            val ciudad = jsonObject.get("ciudad").asString
            val latitud = jsonObject.get("latitud").asDouble
            val longitud = jsonObject.get("longitud").asDouble

            val imagenes = jsonObject.get("imagenes").asJsonArray
            val imageArray = ArrayList<String>()
            imagenes.forEach {
                imageArray.add(it.asString)
            }
            val baños = jsonObject.get("baños").asInt

            return Local(id, disponible, tipo, superficie, precio, propietario, descripcion,
                    direccion, ciudad, latitud, longitud, imageArray, baños)
        }
    }

}
