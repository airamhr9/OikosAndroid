package com.example.oikos.fichaInmueble

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.example.oikos.R
import com.example.oikos.ui.search.FichaMapFragment
import com.google.android.material.tabs.TabLayout
import objects.DatosInmueble

class FichaInmuebleFragment : Fragment() {

    private lateinit var imageViewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var datosFicha : DatosInmueble

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_ficha_inmueble, container, false)

        datosFicha = arguments?.getSerializable("inmueble") as DatosInmueble

        setData(root, datosFicha)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(requireContext(), datosFicha.images)
        imageViewPager = view.findViewById(R.id.image_viewpager)
        tabLayout = view.findViewById(R.id.tab_layout)
        imageViewPager.adapter = adapter
        tabLayout.setupWithViewPager(imageViewPager)

    }


    //TODO: CAMBIA CON EL MODELO
    fun setData(view : View, data: DatosInmueble){
        val priceText = view.findViewById<TextView>(R.id.ficha_precio)
        val addressText = view.findViewById<TextView>(R.id.ficha_direccion_completa)
        val typeCard = view.findViewById<CardView>(R.id.ficha_tipo_tarjeta)
        val availableText = view.findViewById<TextView>(R.id.ficha_tipo_texto)
        val numBaths = view.findViewById<TextView>(R.id.ficha_baños)
        val numRooms = view.findViewById<TextView>(R.id.ficha_habitaciones)
        val size = view.findViewById<TextView>(R.id.ficha_superficie)
        val hasGarage = view.findViewById<TextView>(R.id.ficha_garaje)
        val description = view.findViewById<TextView>(R.id.ficha_descripcion)
        val landlordName = view.findViewById<TextView>(R.id.ficha_propietario_name)
        val landlordMail = view.findViewById<TextView>(R.id.ficha_propietario_mail)

        priceText.text = "${data.precio}€"
        addressText.text = "${data.direccion}"
        if(data.tipo == "Alquiler") {
            //TODO(cambiar a valores en Res/Values)
            typeCard.setCardBackgroundColor(Color.parseColor("#42a5f5"))
        }
            //TODO(cambiar a valores en Res/Values)
        availableText.text = data.tipo
        //numBaths.text = "Baños: ${data.baños}"
        //numRooms.text = "Habitaciones: ${data.habitaciones}"
        size.text = "Superficie: ${data.superficie}m\u00B2"
        //hasGarage.text = if(data.garaje) "Garaje: Sí" else "Garaje: No"
        description.text = data.descripcion
        landlordName.text = data.propietario.nombre
        landlordMail.text = data.propietario.mail

    }
}
