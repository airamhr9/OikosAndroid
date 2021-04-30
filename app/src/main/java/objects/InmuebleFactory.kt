package objects

import com.google.gson.JsonObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class InmuebleFactory {
    val Piso = "piso"
    val Garaje = "garaje"
    val Habitacion = "habitacion"
    val Local = "local"

    fun new(jsonObject: JsonObject) : DatosInmueble {
        return when(jsonObject.get("modelo").asString){
            Piso -> fromJsonPiso(jsonObject)
            Garaje -> fromJsonGaraje(jsonObject)
            Habitacion -> fromJsonHabitacion(jsonObject)
            Local -> fromJsonLocal(jsonObject)
            else -> throw InputMismatchException()
        }
    }

    fun new(jsonObject: JsonObject, modelo : String) : DatosInmueble {
        return when(modelo){
             Piso -> fromJsonPiso(jsonObject)
             Garaje -> fromJsonGaraje(jsonObject)
             Habitacion -> fromJsonHabitacion(jsonObject)
             Local -> fromJsonLocal(jsonObject)
            else -> throw InputMismatchException()
        }
    }

    fun new(
            id: Int, disponible: Boolean, tipo: String, superficie: Int, precio: Double,
            propietario: Usuario, descripcion: String, direccion: String, ciudad: String,
            latitud: Double, longitud: Double, imagenes: ArrayList<String>, habitaciones: Int,
            baños: Int, garaje: Boolean,
    ): Piso {
        return Piso(id, disponible, tipo, superficie, precio, propietario, descripcion, direccion,
                ciudad, latitud, longitud, imagenes, habitaciones, baños, garaje)
    }

    fun new(
            id: Int, disponible: Boolean, tipo: String, superficie: Int, precio: Double,
            propietario: Usuario, descripcion: String, direccion: String, ciudad: String,
            latitud: Double, longitud: Double, imagenes: ArrayList<String>, baños: Int,
    ) : Local {
        return Local(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imagenes, baños)

    }

    fun new(id: Int,
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
             imagenes: ArrayList<String>,) : Garaje {
        return Garaje(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imagenes)
    }

    fun new(id: Int,
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
            habitaciones: Int,
            baños: Int,
            garaje: Boolean,
            numCompañeros: Int,
    ) : Habitacion {
        return Habitacion(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imagenes, habitaciones, baños, garaje, numCompañeros)
    }

    private fun fromJsonPiso(jsonObject: JsonObject): Piso {
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

    private fun fromJsonHabitacion(jsonObject: JsonObject): Habitacion {
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
        val numCompañeros = jsonObject.get("numCompañeros").asInt

        return Habitacion(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imageArray, habitaciones, baños, garaje, numCompañeros)
    }

    private fun fromJsonGaraje(jsonObject: JsonObject): Garaje {
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

        return Garaje(id, disponible, tipo, superficie, precio, propietario, descripcion,
                direccion, ciudad, latitud, longitud, imageArray)
    }


    private fun fromJsonLocal(jsonObject: JsonObject): Local {
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