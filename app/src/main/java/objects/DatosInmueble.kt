package objects

import com.example.oikos.R
import com.google.gson.JsonObject
import java.io.Serializable

class DatosInmueble (var disponible: Boolean,
                     var superficie: Int,
                      var precio: Double,
                      var direccion: String,
                      var latitud: Double,
                      var longitud: Double,
                      var habitaciones: Int,
                      var baños: Int,
                      var garaje: Boolean,
                      var propietario: Usuario,
                      var descripcion: String?,
                      var tipo : String,
                      val images:  ArrayList<String>) : Serializable {
    var id: Int = 0

    fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", this.id)
        result.addProperty("disponible", this.disponible)
        // Falta añadir el tipo
        result.addProperty("superficie", this.superficie)
        result.addProperty("precio", this.precio)
        result.addProperty("direccion", this.direccion)
        result.addProperty("latitud", this.latitud)
        result.addProperty("longitud", this.longitud)
        result.addProperty("habitaciones", this.habitaciones)
        result.addProperty("baños", this.baños)
        result.addProperty("garaje", this.garaje)
        result.add("propietario", this.propietario.toJson())
        result.addProperty("descripcion", this.descripcion)
        // Falta añadir las imagenes
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): DatosInmueble {
            val disponible = jsonObject.get("disponible").asBoolean
            val superficie = jsonObject.get("superficie").asInt
            val precio = jsonObject.get("precio").asDouble
            val direccion = jsonObject.get("direccion").asString
            val latitud = jsonObject.get("latitud").asDouble
            val longitud = jsonObject.get("longitud").asDouble
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean
            val propietario = Usuario.fromJson(jsonObject.get("propietario").asJsonObject)
            val descripcion = jsonObject.get("descripcion").asString.toString()
            val tipo = jsonObject.get("tipo").asString.toString()
            val imageArray = ArrayList<String>()
            val imagenes = jsonObject.get("imagenes").asJsonArray
            imagenes.forEach {
                imageArray.add(it.asString)
            }
            val datosInmueble = DatosInmueble(disponible, superficie, precio, direccion, latitud, longitud,
                    habitaciones, baños, garaje, propietario, descripcion, tipo, imageArray)
            datosInmueble.id = jsonObject.get("id").asInt
            return datosInmueble
        }
    }
                          }

