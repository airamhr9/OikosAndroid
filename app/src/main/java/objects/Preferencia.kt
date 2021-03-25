package objects

import com.google.gson.JsonObject
import java.io.Serializable

class Preferencia(
        var superficie_min: Int,
         var superficie_max: Int,
        var precio_min: Double,
         var precio_max: Double,
         var habitaciones: Int?,
         var baños: Int?,
         var garaje: Boolean,
         var ciudad: String,
         var usuario: Usuario,
         var tipo: Tipo,
) :Serializable {

    fun toJson(): JsonObject {
        val result = JsonObject()
        result.addProperty("superficie_min", this.superficie_min)
        result.addProperty("superficie_max", this.superficie_max)
        result.addProperty("precio_min", this.precio_min)
        result.addProperty("precio_max", this.precio_max)
        result.addProperty("habitaciones", this.habitaciones)
        result.addProperty("baños", this.baños)
        result.addProperty("garaje", this.garaje)
        result.addProperty("ciudad", this.ciudad)
        result.addProperty("tipo",this.tipo )
        result.add("usuario", this.usuario.toJson())
        return result
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Preferencia {
            val superficie_min = jsonObject.get("superficie_min").asInt
            val superficie_max = jsonObject.get("superficie_max").asInt
            val precio_min = jsonObject.get("precio_min").asDouble
            val precio_max = jsonObject.get("precio_max").asDouble
            val habitaciones = jsonObject.get("habitaciones").asInt
            val baños = jsonObject.get("baños").asInt
            val garaje = jsonObject.get("garaje").asBoolean
            val ciudad = jsonObject.get("ciudad").asString.toString()
            val tipo = jsonObject.get("tipo").asString.toString()
            val usuario = Usuario.fromJson(jsonObject.get("usuario").asJsonObject)
            return Preferencia(superficie_min, superficie_max, precio_min, precio_max, habitaciones,baños, garaje, ciudad, usuario,tipo)
        }
    }
}