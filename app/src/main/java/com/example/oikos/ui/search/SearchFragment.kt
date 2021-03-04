package com.example.oikos.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.oikos.R
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView


class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mapView : MapView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
                ViewModelProvider(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)


        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene()
        mapView.setOnReadyListener {
            // This will be called each time after this activity is resumed.
            // It will not be called before the first map scene was loaded.
            // Any code that requires map data may not work as expected beforehand.
            println("Map ready")
        }
        return root
    }

    private fun loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
            if (mapError == null) {
                val distanceInMeters = (1000 * 10).toDouble()
                mapView.camera.lookAt(
                        GeoCoordinates(52.530932, 13.384915), distanceInMeters)
            } else {
                println("Loading map failed")
            }
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


}