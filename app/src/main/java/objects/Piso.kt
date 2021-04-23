package objects

import com.google.gson.JsonObject

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
                imagenes: ArrayList<String>,
                var habitaciones: Int,
                var baños: Int,
                var garaje: Boolean,
) : DatosInmueble(id, disponible, tipo, superficie, precio, propietario, descripcion,
        direccion, ciudad, latitud, longitud, imagenes) {

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
            val imageArray = ArrayList<String>()
            imagenes.forEach {
                imageArray.add(it.asString)
            }
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean

            return Piso(id, disponible, tipo, superficie, precio, propietario, descripcion,
                    direccion, ciudad, latitud, longitud, imageArray, habitaciones, baños, garaje)
        }
    }
}