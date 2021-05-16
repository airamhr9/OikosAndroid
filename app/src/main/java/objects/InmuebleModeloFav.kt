package objects

import com.google.gson.JsonObject
import java.io.Serializable

class InmuebleModeloFav(val inmueble: DatosInmueble, val modelo: String, var esFavorito: Boolean, var nota: String, var orden : Int) : Serializable {
    fun toJson () : JsonObject {
        val json = inmueble.toJson()
        json.addProperty("modelo", modelo)
        json.addProperty("favorito", esFavorito)
        json.addProperty("nota", nota)
        json.addProperty("orden", orden)
        return json
    }

    fun toInmuebleWithModelo() : InmuebleWithModelo =  InmuebleWithModelo(inmueble, modelo)
}
