package com.example.oikos.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmuebleActivity
import com.here.sdk.core.*
import com.here.sdk.gestures.TapListener
import com.here.sdk.mapview.*
import objects.DatosInmueble
import objects.Usuario
import java.net.URL

class MapSearchFragment : Fragment() {

    private lateinit var mapSearchViewModel: MapSearchViewModel
    private lateinit var mapView : MapView
    private lateinit var mapCard : CardView
    private lateinit var listaInmuebles : ArrayList<DatosInmueble>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mapSearchViewModel =
                ViewModelProvider(this).get(MapSearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map_search, container, false)

        mapCard = root.findViewById(R.id.map_card)

        listaInmuebles = requireArguments().getSerializable("inmueble") as ArrayList<DatosInmueble>

        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene()
        mapView.setOnReadyListener {
            setTapGestureHandler()
            val coordList = listaInmuebles.map { GeoCoordinates(it.latitud, it.longitud) }
            drawCircles(coordList)
        }

        return root
    }

    private fun loadMapScene() {
        mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
            if (mapError == null) {
                val distanceInMeters = (1000 * 10).toDouble()
                mapView.camera.lookAt(
                        GeoCoordinates(
                                listaInmuebles.first().latitud,
                                listaInmuebles.first().longitud
                        ), distanceInMeters)
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
        if (mapMarkerList.size == 0 ) {
            if(mapCard.visibility == View.VISIBLE){
                slideDown(mapCard)
                mapCard.visibility = View.INVISIBLE
            }
            return
        }
        val topmostMapMarker = mapMarkerList[0]
        val selectedInmueble = listaInmuebles.first {
            it.latitud == topmostMapMarker.coordinates.latitude && it.longitud == topmostMapMarker.coordinates.longitude}

        if (mapCard.visibility == View.INVISIBLE){
            mapCard.visibility = View.VISIBLE
            slideUp(mapCard)
            setCardData(selectedInmueble)
        } else {
            setCardData(selectedInmueble)
        }
        mapCard.setOnClickListener {
            val intent = Intent(this.context, FichaInmuebleActivity :: class.java)
            intent.putExtra("inmueble", selectedInmueble)
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

    fun setCardData(inmueble: DatosInmueble){
        val numImag = view?.findViewById<TextView>(R.id.map_card_num_imag)
        val price = view?.findViewById<TextView>(R.id.map_card_price)
        val type = view?.findViewById<TextView>(R.id.map_card_type)
        val imageView = view?.findViewById<ImageView>(R.id.map_card_image)


        //TODO(solo para emulador)
        var url = URL(inmueble.images.first())
        url = URL("http://10.0.2.2:9000${url.path}")

        Glide.with(requireContext()).asBitmap().load(url.toString()).into(imageView!!)

        numImag?.text = "${inmueble.images.size} imágenes"
        price?.text = "${inmueble.precio}€"
        type?.text = inmueble.tipo
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