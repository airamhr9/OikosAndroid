package objects

import com.here.sdk.core.GeoCoordinates
import java.io.Serializable

data class GeoCoordsSerializable (val latitud : Double, val longitud : Double) :  Serializable
