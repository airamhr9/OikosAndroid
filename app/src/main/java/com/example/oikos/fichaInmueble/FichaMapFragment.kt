package com.example.oikos.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.oikos.R
import com.here.sdk.core.*
import com.here.sdk.mapview.*
import objects.DatosInmueble

class FichaMapFragment : Fragment() {

    private lateinit var mapSearchViewModel: MapSearchViewModel
    private lateinit var mapView : MapView
    private lateinit var inmueble: DatosInmueble

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mapSearchViewModel =
                ViewModelProvider(this).get(MapSearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map_search, container, false)

        inmueble = requireArguments().getSerializable("inmueble") as DatosInmueble

        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene()
        mapView.setOnReadyListener {
            drawCircles(GeoCoordinates(inmueble.latitud, inmueble.longitud))
        }

        return root
    }

    private fun loadMapScene() {
        mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
            if (mapError == null) {
                val distanceInMeters = (1000 * 10).toDouble()
                mapView.camera.lookAt(
                        GeoCoordinates(inmueble.latitud, inmueble.longitud), distanceInMeters)
            } else {
                println("Loading map failed")
            }
        }
    }

    private fun drawCircles(coordInmueble: GeoCoordinates){
        val mapImage = MapImageFactory.fromResource(context?.resources, R.drawable.map_marker)
        mapView.mapScene.addMapPolygon(createMapCircle(coordInmueble))
        val mapMarker = MapMarker(coordInmueble, mapImage)
        mapView.mapScene.addMapMarker(mapMarker)
    }

    private fun createMapCircle(coordinates: GeoCoordinates) : MapPolygon{
        val radiusInMeters = 250.0
        val geoCircle = GeoCircle(GeoCoordinates(coordinates.latitude, coordinates.longitude), radiusInMeters);
        val geoPolygon = GeoPolygon(geoCircle);
        val fillColor : Color = Color.valueOf(0.827f, 0.184f, 0.184f, 0.5f) // RGBA
        return MapPolygon(geoPolygon, fillColor)
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
}
