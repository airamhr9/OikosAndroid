package objects

import com.google.gson.JsonObject

class Favorito(
    var usuario: Usuario,
    var inmueble : InmuebleWithModelo,
    var notas: String,
    var orden: Int
) {

    fun toJson(): JsonObject {
        val result = JsonObject()
        result.add("usuario", usuario.toJson())
        result.add("inmueble", inmueble.toJson())
        result.addProperty("notas", notas)
        result.addProperty("orden", orden)
        return result
    }

    companion object {

        fun fromJson(jsonObject: JsonObject): Favorito {
            val usuario = Usuario.fromJson(jsonObject.getAsJsonObject("usuario"))

            val inmuebleJson = jsonObject.getAsJsonObject("inmueble")
            val inmueble = InmuebleFactory().new (inmuebleJson, inmuebleJson.get("modelo").asString)

            val notas = jsonObject.get("notas").asString
            val orden = jsonObject.get("orden").asInt

            return Favorito(usuario, InmuebleWithModelo(inmueble, inmuebleJson.get("modelo").asString) , notas, orden)
        }

    }
}