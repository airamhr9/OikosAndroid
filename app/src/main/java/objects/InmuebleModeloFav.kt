package objects

import com.google.gson.JsonObject
import java.io.Serializable

class InmuebleModeloFav (val inmueble : DatosInmueble, val modelo : String, var esFavorito: Boolean) : Serializable {
    fun toJson () : JsonObject {
        val json = inmueble.toJson()
        json.addProperty("modelo", modelo)
        json.addProperty("favorito", esFavorito)
        return json
    }

    fun toInmuebleWithModelo() : InmuebleWithModelo =  InmuebleWithModelo(inmueble, modelo)
}
