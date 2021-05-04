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

        val listaImagenes = JsonArray()
        for (imagen:String in imagenes) {
            listaImagenes.add(imagen)
        }
        result.add("imagenes", listaImagenes)

        return result
    }
}

