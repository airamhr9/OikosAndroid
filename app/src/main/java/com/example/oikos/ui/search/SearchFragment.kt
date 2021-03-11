package com.example.oikos.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.oikos.R
import com.here.sdk.core.*
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.*

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mapView : MapView
    private lateinit var mapCard : CardView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
                ViewModelProvider(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        mapCard = root.findViewById(R.id.map_card)

        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene()
        mapView.setOnReadyListener {
            setTapGestureHandler()
            val coordList = listOf(GeoCoordinates(52.530932, 13.384915), GeoCoordinates(52.520932, 13.384915), GeoCoordinates(52.530932, 13.394915), GeoCoordinates(52.550932, 13.39915),
                    GeoCoordinates(52.560932, 13.44915), GeoCoordinates(52.580932, 13.384915), GeoCoordinates(52.530932, 13.404915), GeoCoordinates(52.530932, 13.584915))
            drawCircles(coordList)
        }

        return root
    }

    private fun loadMapScene() {
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

    private fun drawCircles(coordList: List<GeoCoordinates>){
        val mapImage = MapImageFactory.fromResource(context?.resources, R.drawable.map_marker)
        for(coord in coordList){
            mapView.mapScene.addMapPolygon(createMapCircle(coord))
            val mapMarker = MapMarker(coord, mapImage)
            mapView.mapScene.addMapMarker(mapMarker)
        }
    }

    private fun createMapCircle(coordinates: GeoCoordinates) : MapPolygon{
        val radiusInMeters = 250.0
        val geoCircle = GeoCircle(GeoCoordinates(coordinates.latitude, coordinates.longitude), radiusInMeters);
        val geoPolygon = GeoPolygon(geoCircle);
        val fillColor : Color = Color.valueOf(0.827f, 0.184f, 0.184f, 0.5f) // RGBA
       return MapPolygon(geoPolygon, fillColor)
    }

    private fun setTapGestureHandler() {
        mapView.gestures.tapListener = TapListener { touchPoint -> pickMapMarker(touchPoint) }
    }

    private fun pickMapMarker(touchPoint: Point2D) {
        val radiusInPixel = 2f
        mapView.pickMapItems(touchPoint, radiusInPixel.toDouble()) {
            if(it != null)
                onPickMapItems(it)
        }
    }

    private fun onPickMapItems(pickMapItemsResult: PickMapItemsResult) {
        val mapMarkerList = pickMapItemsResult.markers
        if (mapMarkerList.size == 0) {
            slideDown(mapCard)
            mapCard.visibility = View.INVISIBLE
            return
        }
        val topmostMapMarker = mapMarkerList[0]
        //JColorChooser.showDialog("Map marker picked:", "Location: " +
        //      topmostMapMarker.coordinates.latitude + ", " +
        //    topmostMapMarker.coordinates.longitude)
        if (mapCard.visibility == View.INVISIBLE){
            mapCard.visibility = View.VISIBLE
            slideUp(mapCard)
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

    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
                0f,  // fromXDelta
                0f,  // toXDelta
                view.height.toFloat(),  // fromYDelta
                0f) // toYDelta
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
                view.height.toFloat()) // toYDelta
        animate.duration = 100
        view.startAnimation(animate)
    }

}