package objects

import com.google.gson.JsonObject
import java.io.Serializable

class InmuebleWithModelo (val inmueble : DatosInmueble, val modelo : String) : Serializable {
    fun toJson () : JsonObject {
        val json = inmueble.toJson()
        json.addProperty("modelo", modelo)
        return json
    }

}