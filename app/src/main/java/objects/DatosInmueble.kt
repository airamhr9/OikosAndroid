package objects

import com.example.oikos.R
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.Serializable
abstract class DatosInmueble(var id: Int,
                             var disponible: Boolean,
                             var tipo: String,
                             var superficie: Int,
                             var precio: Double,
                             var propietario: Usuario,
                             var descripcion: String,
                             var direccion: String,
                             var ciudad: String,
                             var latitud: Double,
                             var longitud: Double,
                             var fecha: String,
                             var contadorVisitas: Int,
                             var imagenes: ArrayList<String>,
) : Serializable {

    private fun generarJsonBasico(): JsonObject {
        val result = JsonObject()
        result.addProperty("id", id)
        result.addProperty("disponible", disponible)
        result.addProperty("tipo", tipo)
        result.addProperty("precio", precio)
        result.addProperty("direccion", direccion)
        result.addProperty("ciudad", ciudad)
        result.addProperty("latitud", latitud)
        result.addProperty("longitud", longitud)
        return result
    }

    open fun toJson(): JsonObject {
        val result = generarJsonBasico()

        result.addProperty("superficie", superficie)
        result.add("propietario", propietario.toJson())
        result.addProperty("descripcion", descripcion)
        result.addProperty("fecha", fecha)
        result.addProperty("contadorVisitas", contadorVisitas)

        val listaImagenes = JsonArray()
        for (imagen:String in imagenes) {
            listaImagenes.add(imagen)
        }
        result.add("imagenes", listaImagenes)

        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatosInmueble

        if (id != other.id) return false
        if (disponible != other.disponible) return false
        if (tipo != other.tipo) return false
        if (superficie != other.superficie) return false
        if (precio != other.precio) return false
        if (propietario != other.propietario) return false
        if (descripcion != other.descripcion) return false
        if (direccion != other.direccion) return false
        if (ciudad != other.ciudad) return false
        if (latitud != other.latitud) return false
        if (longitud != other.longitud) return false
        if (fecha != other.fecha) return false
        if (contadorVisitas != other.contadorVisitas) return false
        if (imagenes != other.imagenes) return false

        return true
    }
}

