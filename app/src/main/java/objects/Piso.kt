package objects

import com.google.gson.JsonObject
import java.io.Serializable

open class Piso(id: Int,
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
                fecha: String,
                contadorVisitas: Int,
                imagenes: ArrayList<String>,
                var habitaciones: Int,
                var baños: Int,
                var garaje: Boolean,
) : Serializable, DatosInmueble(id, disponible, tipo, superficie, precio, propietario, descripcion,
        direccion, ciudad, latitud, longitud, fecha, contadorVisitas, imagenes) {

    override fun toJson(): JsonObject {
        val result = super.toJson()
        result.addProperty("habitaciones", habitaciones)
        result.addProperty("baños", baños)
        result.addProperty("garaje", garaje)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Piso

        if (habitaciones != other.habitaciones) return false
        if (baños != other.baños) return false
        if (garaje != other.garaje) return false

        return true
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Piso {
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
            val fecha = jsonObject.get("fecha").asString
            val contadorVisitas = jsonObject.get("contadorVisitas").asInt
            val imageArray = ArrayList<String>()
            imagenes.forEach {
                imageArray.add(it.asString)
            }
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean

            return Piso(id, disponible, tipo, superficie, precio, propietario, descripcion,
                    direccion, ciudad, latitud, longitud, fecha, contadorVisitas, imageArray, habitaciones, baños, garaje)
        }
    }
}