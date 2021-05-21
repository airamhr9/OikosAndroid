package com.example.oikos.ui.search.localized

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.bumptech.glide.Glide
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmuebleActivity
import com.example.oikos.serverConnection.PlatformPositioningProvider
import com.google.gson.JsonParser
import com.here.sdk.core.*
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.*
import objects.InmuebleFactory
import objects.InmuebleWithModelo
import org.json.JSONArray
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class LocalizedSearch : AppCompatActivity() {
    private lateinit var mapView : MapView
    private lateinit var mapCard : CardView
    private lateinit var listaInmuebles : ArrayList<InmuebleWithModelo>
    private var inmueblesDisplayed : Boolean = false
    private lateinit var mapMarkers : ArrayList<MapMarker>
    private lateinit var mapPolygons : ArrayList<MapPolygon>
    private var searchMarker : MapMarker? = null
    private lateinit var hideButton : AppCompatButton
    private lateinit var infoCard : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localized_search)

        supportActionBar?.title = "Búsqueda Geolocalizada"

        mapCard = findViewById(R.id.map_localized_card)
        listaInmuebles = ArrayList()
        mapMarkers = ArrayList()
        mapPolygons = ArrayList()
        mapView = findViewById(R.id.map_localized_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene()
        mapView.setOnReadyListener {
            setTapGestureHandler()
            getResults()
        }

        infoCard = findViewById(R.id.map_localized_info_card)
        hideButton = findViewById(R.id.hide_info_card)
        hideButton.setOnClickListener {
            infoCard.visibility = View.INVISIBLE
        }
    }

    private fun loadMapData(){
        val coordList = listaInmuebles.map { GeoCoordinates(it.inmueble.latitud, it.inmueble.longitud) }
        drawCircles(coordList)
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

    private fun drawCircles(coordList: List<GeoCoordinates>){
        val mapImage = MapImageFactory.fromResource(
            applicationContext.resources,
            R.drawable.map_marker
        )
        for(coord in coordList){
            mapView.mapScene.addMapPolygon(createMapCircle(coord))
            val mapMarker = MapMarker(coord, mapImage)
            mapMarkers.add(mapMarker)
            mapView.mapScene.addMapMarker(mapMarker)
        }
    }

    private fun createMapCircle(coordinates: GeoCoordinates) : MapPolygon {
        val radiusInMeters = 250.0
        val geoCircle = GeoCircle(
            GeoCoordinates(coordinates.latitude, coordinates.longitude),
            radiusInMeters
        );
        val geoPolygon = GeoPolygon(geoCircle);
        val fillColor : Color = Color.valueOf(0.827f, 0.184f, 0.184f, 0.5f) // RGBA
        val polygon = MapPolygon(geoPolygon, fillColor)
        mapPolygons.add(polygon)
        return polygon
    }

    private fun setTapGestureHandler() {
        mapView.gestures.tapListener = TapListener { touchPoint -> pickMapMarker(touchPoint) }
    }

    private fun pickMapMarker(touchPoint: Point2D) {
        val radiusInPixel = 2f
        mapView.pickMapItems(touchPoint, radiusInPixel.toDouble()) {
            if(it != null)
                onPickMapItems(touchPoint, it)
        }
    }

    private fun onPickMapItems(touchPoint: Point2D, pickMapItemsResult: PickMapItemsResult) {
        val mapMarkerList = pickMapItemsResult.markers
        if (mapMarkerList.size == 0 ) {
            if(mapCard.visibility == View.VISIBLE){
                slideDown(mapCard)
                mapCard.visibility = View.INVISIBLE
            } else {
                val mapImage = MapImageFactory.fromResource(
                    applicationContext.resources,
                    R.drawable.map_marker
                )
                val touchCoordinates = mapView.viewToGeoCoordinates(touchPoint)
                if(searchMarker != null) mapView.mapScene.removeMapMarker(searchMarker!!)
                if(touchCoordinates != null){
                    searchMarker = MapMarker(touchCoordinates, mapImage)
                    mapView.mapScene.addMapMarker(searchMarker!!)
                    AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_menu_search)
                        .setTitle("Nueva búsqueda")
                        .setMessage("¿Desea buscar en esta zona?")
                        .setPositiveButton("Sí"
                        ) { _, _ -> getResultsWithCoords(touchCoordinates)
                        }
                        .setNegativeButton("No"){ _,_ ->
                            mapView.mapScene.removeMapMarker(searchMarker!!)
                        }
                        .show()
                }
            }
            return
        }
        val topmostMapMarker = mapMarkerList[0]
        val selectedInmueble = listaInmuebles.first {
            it.inmueble.latitud == topmostMapMarker.coordinates.latitude && it.inmueble.longitud == topmostMapMarker.coordinates.longitude}

        if (mapCard.visibility == View.INVISIBLE){
            mapCard.visibility = View.VISIBLE
            slideUp(mapCard)
            setCardData(selectedInmueble)
        } else {
            setCardData(selectedInmueble)
        }
        mapCard.setOnClickListener {
            val intent = Intent(this, FichaInmuebleActivity::class.java)
            intent.putExtra("inmueble", selectedInmueble.inmueble)
            intent.putExtra("modelo", selectedInmueble.modelo)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    fun setCardData(inmuebleData: InmuebleWithModelo){
        val inmueble = inmuebleData.inmueble
        val numImag = findViewById<TextView>(R.id.map_card_localized_num_imag)
        val price = findViewById<TextView>(R.id.map_card_localized_price)
        val type = findViewById<TextView>(R.id.map_card_localized_type)
        val typeCard = findViewById<CardView>(R.id.map_card_localized_type_card)
        val imageView = findViewById<ImageView>(R.id.map_card_localized_image)


        //TODO(solo para emulador)
        if(inmueble.imagenes.isNotEmpty()) {
            var url = URL(inmueble.imagenes.first())
            url = URL("http://10.0.2.2:9000${url.path}")
            Glide.with(this).asBitmap().load(url.toString()).into(imageView!!)
        }

        numImag?.text = "${inmueble.imagenes.size} imágenes"
        price?.text = "${inmueble.precio}€"
        type?.text = inmueble.tipo
        if(type?.text == "Alquiler"){
            typeCard?.setCardBackgroundColor(android.graphics.Color.parseColor("#42a5f5"))
        } else {
            typeCard?.setCardBackgroundColor(android.graphics.Color.parseColor("#4caf50"))
        }
    }

    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 100
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    private fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 100
        view.startAnimation(animate)
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

    private fun getResults(){
            if (searchMarker!= null) mapView.mapScene.removeMapMarker(searchMarker!!)
            val platformPositioningProvider = PlatformPositioningProvider(this);
            val located = platformPositioningProvider.startLocating(object :
                PlatformPositioningProvider.PlatformLocationListener {
                override fun onLocationUpdated(location: android.location.Location?) {
                    val currentLocation = location?.let { convertLocation(it) }
                    AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
                        .addQueryParameter("coordenada", "true")
                        .addQueryParameter("x", currentLocation?.coordinates?.latitude.toString())
                        .addQueryParameter("y", currentLocation?.coordinates?.longitude.toString())
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(object : JSONArrayRequestListener {
                            override fun onResponse(response: JSONArray) {
                                // do anything with response
                                var i = 0
                                println("we have response")
                                listaInmuebles.clear()
                                while (i < response.length()) {
                                    println("search result $i ${response[i]}")
                                    val inmuebleJson = JsonParser.parseString(
                                            response[i].toString()
                                    ).asJsonObject
                                    val modelo = inmuebleJson.get("modelo").asString!!
                                    listaInmuebles.add(
                                        InmuebleWithModelo(
                                            InmuebleFactory().new(
                                                inmuebleJson,
                                                modelo
                                            ), modelo)
                                    )
                                    i++
                                }
                                inmueblesDisplayed = true
                                loadMapData()
                                if(listaInmuebles.size > 0)
                                mapView.camera.lookAt(
                                    GeoCoordinates(
                                        listaInmuebles.first().inmueble.latitud,
                                        listaInmuebles.first().inmueble.longitud
                                    ), (1000 * 10).toDouble()
                                )
                                /*
                                customAdapter.notifyDataSetChanged()
                                seeInMapButton.isEnabled = true
                                loadingCircle.visibility = View.GONE
                                resultLayout.visibility = View.VISIBLE
                                 */
                            }

                            override fun onError(error: ANError) {
                                Toast.makeText(
                                    applicationContext,
                                    "Error cargando inmuebles",
                                    Toast.LENGTH_LONG
                                ).show()
                                inmueblesDisplayed = false
                            }
                        })
                }
            })
            if(!located){
                    AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
                        .addQueryParameter("default", "true")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(object : JSONArrayRequestListener {
                            override fun onResponse(response: JSONArray) {
                                // do anything with response
                                var i = 0
                                println("we have response")
                                listaInmuebles.clear()
                                while (i < response.length()) {
                                    val inmuebleJson = JsonParser.parseString(
                                            response[i].toString()
                                    ).asJsonObject
                                    val modelo = inmuebleJson.get("modelo").asString!!
                                    listaInmuebles.add(
                                        InmuebleWithModelo(
                                            InmuebleFactory().new(
                                                    inmuebleJson,
                                                    modelo
                                            ), modelo)
                                    )
                                    i++
                                }
                                loadMapData()
                                inmueblesDisplayed = true
                                /*
                                customAdapter.notifyDataSetChanged()
                                seeInMapButton.isEnabled = true
                                loadingCircle.visibility = View.GONE
                                resultLayout.visibility = View.VISIBLE
                                 */
                            }

                            override fun onError(error: ANError) {
                                Toast.makeText(
                                    applicationContext,
                                    "Error cargando inmuebles",
                                    Toast.LENGTH_LONG
                                ).show()
                                inmueblesDisplayed = false
                            }
                        })
            }
    }

    private fun getResultsWithCoords(geoCoordinates: GeoCoordinates){
        mapView.mapScene.removeMapMarker(searchMarker!!)
        mapMarkers.forEach{mapView.mapScene.removeMapMarker(it)}
        mapPolygons.forEach{mapView.mapScene.removeMapPolygon(it)}
        AndroidNetworking.get("http://10.0.2.2:9000/api/inmueble/")
            .addQueryParameter("coordenada", "true")
            .addQueryParameter("x", geoCoordinates.latitude.toString())
            .addQueryParameter("y", geoCoordinates.longitude.toString())
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    var i = 0
                    println("we have response")
                    listaInmuebles.clear()
                    while (i < response.length()) {
                        val inmuebleJson = JsonParser.parseString(
                                response[i].toString()
                        ).asJsonObject
                        val modelo = inmuebleJson.get("modelo").asString!!
                        listaInmuebles.add(
                            InmuebleWithModelo(
                                InmuebleFactory().new(
                                        inmuebleJson,
                                        modelo
                                ), modelo)
                        )
                        i++
                    }
                    inmueblesDisplayed = true
                    loadMapData()
                    if(listaInmuebles.isNotEmpty()){
                        mapView.camera.lookAt(
                            GeoCoordinates(
                                listaInmuebles.first().inmueble.latitud,
                                listaInmuebles.first().inmueble.longitud
                            ), (1000 * 10).toDouble()
                        )
                    } else {
                        AlertDialog.Builder(this@LocalizedSearch)
                            .setIcon(android.R.drawable.ic_menu_search)
                            .setTitle("Sin inmuebles en esta zona")
                            .setMessage("Pruebe en otro lugar")
                            .setPositiveButton("Ok"
                            ) { _, _ ->
                                mapView.mapScene.removeMapMarker(searchMarker!!)
                            }
                            .show()
                    }
                }

                override fun onError(error: ANError) {
                    Toast.makeText(
                        applicationContext,
                        "Error cargando inmuebles",
                        Toast.LENGTH_LONG
                    ).show()
                    inmueblesDisplayed = false
                }
            })
        }

}