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
import objects.Habitacion
import objects.Local
import objects.Piso

class FichaInmuebleFragment : Fragment() {

    private lateinit var datosFicha : DatosInmueble

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_ficha_inmueble, container, false)

        datosFicha = arguments?.getSerializable("inmueble") as DatosInmueble
        val modelo = arguments?.getString("modelo") as String

        setData(root, datosFicha, modelo)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(requireContext(), datosFicha.imagenes)
        val imageViewPager : ViewPager = view.findViewById(R.id.image_viewpager)
        val tabLayout : TabLayout = view.findViewById(R.id.tab_layout)
        imageViewPager.adapter = adapter
        tabLayout.setupWithViewPager(imageViewPager)

    }


    fun setData(view : View, data: DatosInmueble, modelo : String){
        val priceText = view.findViewById<TextView>(R.id.ficha_precio)
        val addressText = view.findViewById<TextView>(R.id.ficha_direccion_completa)
        val typeCard = view.findViewById<CardView>(R.id.ficha_tipo_tarjeta)
        val availableText = view.findViewById<TextView>(R.id.ficha_tipo_texto)
        val size = view.findViewById<TextView>(R.id.ficha_superficie)
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
        size.text = "Superficie: ${data.superficie}m\u00B2"
        description.text = data.descripcion
        landlordName.text = data.propietario.nombre
        landlordMail.text = data.propietario.mail

        setSpecificData(data, view, modelo)
    }

    private fun setSpecificData(data: DatosInmueble , view: View, modelo: String){
        val numComp = view.findViewById<TextView>(R.id.ficha_num_comp)
        val numBaths = view.findViewById<TextView>(R.id.ficha_baños)
        val numRooms = view.findViewById<TextView>(R.id.ficha_habitaciones)
        val hasGarage = view.findViewById<TextView>(R.id.ficha_garaje)
        numComp.visibility = View.GONE
        numBaths.visibility = View.GONE
        numRooms.visibility = View.GONE
        hasGarage.visibility = View.GONE

        when(modelo) {
            "piso" -> {
                val piso = data as Piso
                numBaths.text = "Baños: ${piso.baños}"
                numRooms.text = "Habitaciones: ${piso.habitaciones}"
                hasGarage.text = if(piso.garaje) "Garaje: Sí" else "Garaje: No"
                numBaths.visibility = View.VISIBLE
                numRooms.visibility = View.VISIBLE
                hasGarage.visibility = View.VISIBLE
            }
            "local" -> {
                val local = data as Local
                numBaths.text = "Baños: ${local.baños}"
                numBaths.visibility = View.VISIBLE
            }
            "habitacion" -> {
                val hab = data as Habitacion
                numBaths.text = "Baños: ${hab.baños}"
                numRooms.text = "Habitaciones: ${hab.habitaciones}"
                numComp.text = "Compañeros: ${hab.numCompañeros}"
                hasGarage.text = if(hab.garaje) "Garaje: Sí" else "Garaje: No"
                numBaths.visibility = View.VISIBLE
                numRooms.visibility = View.VISIBLE
                hasGarage.visibility = View.VISIBLE
                numComp.visibility = View.VISIBLE
            }
        }

    }
}
