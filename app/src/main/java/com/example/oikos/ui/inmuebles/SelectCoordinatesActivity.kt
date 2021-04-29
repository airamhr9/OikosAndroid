package com.example.oikos.ui.inmuebles

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.oikos.R
import com.example.oikos.serverConnection.PlatformPositioningProvider
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Location
import com.here.sdk.core.Point2D
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView
import objects.GeoCoordsSerializable
import java.util.*

class SelectCoordinatesActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var searchMarker: MapMarker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_coordinates)

        supportActionBar?.title = "Selecciona zona"
        mapView = findViewById(R.id.map_localized_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene()
        mapView.setOnReadyListener {
            setTapGestureHandler()
            centerInLocation()
        }
    }

    private fun loadMapScene() {
        mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
            if (mapError == null) {
                val distanceInMeters = (1000 * 10).toDouble()
                mapView.camera.lookAt(
                        GeoCoordinates(40.416729, -3.703339), distanceInMeters
                )
            } else {
                println("Loading map failed")
            }
        }
    }

    private fun setTapGestureHandler() {
        mapView.gestures.tapListener = TapListener { touchPoint -> handleTouch(touchPoint) }
    }

    private fun handleTouch(touchPoint: Point2D) {
        val touchCoordinates = mapView.viewToGeoCoordinates(touchPoint)
        val mapImage = MapImageFactory.fromResource(
                applicationContext.resources,
                R.drawable.map_marker
        )
        if (searchMarker != null) mapView.mapScene.removeMapMarker(searchMarker!!)
        if (touchCoordinates != null) {
            searchMarker = MapMarker(touchCoordinates, mapImage)
            mapView.mapScene.addMapMarker(searchMarker!!)
            AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_search)
                    .setTitle("Seleccionar zona")
                    .setMessage("¿Desea situar su inmueble aquí?")
                    .setPositiveButton("Sí"
                    ) { _, _ -> returnActivityResult(touchCoordinates) }
                    .setNegativeButton("No") { _, _ ->
                        mapView.mapScene.removeMapMarker(searchMarker!!)
                    }
                    .show()
        }
    }

    private fun returnActivityResult(touchCoordinates: GeoCoordinates){
        val returnIntent = Intent()
        returnIntent.putExtra("coords", GeoCoordsSerializable(touchCoordinates.latitude, touchCoordinates.longitude))
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun centerInLocation() {
        val platformPositioningProvider = PlatformPositioningProvider(this);
        platformPositioningProvider.startLocating(object :
                PlatformPositioningProvider.PlatformLocationListener {
            override fun onLocationUpdated(location: android.location.Location?) {
                val currentLocation = location?.let { convertLocation(it) }
                if (currentLocation != null) {
                    mapView.camera.lookAt(
                            GeoCoordinates(
                                    currentLocation.coordinates.latitude,
                                    currentLocation.coordinates.longitude
                            ), (1000 * 10).toDouble()
                    )
                }
            }
        }
        )
    }

    private fun convertLocation(nativeLocation: android.location.Location): Location {
        val geoCoordinates = GeoCoordinates(
                nativeLocation.latitude,
                nativeLocation.longitude,
                nativeLocation.altitude
        )
        val location = Location(geoCoordinates, Date())
        if (nativeLocation.hasBearing()) {
            location.bearingInDegrees = nativeLocation.bearing.toDouble()
        }
        if (nativeLocation.hasSpeed()) {
            location.speedInMetersPerSecond = nativeLocation.speed.toDouble()
        }
        if (nativeLocation.hasAccuracy()) {
            location.horizontalAccuracyInMeters = nativeLocation.accuracy.toDouble()
        }
        return location
    }

}
