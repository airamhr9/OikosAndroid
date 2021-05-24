package objects

import com.google.gson.JsonObject
import java.io.Serializable

class InmuebleWithModelo (val inmueble : DatosInmueble, val modelo : String) : Serializable {
    fun toJson () : JsonObject {
        val json = inmueble.toJson()
        json.addProperty("modelo", modelo)
        return json
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InmuebleWithModelo

        if (inmueble != other.inmueble) return false
        if (modelo != other.modelo) return false

        return true
    }
}