package com.example.oikos.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.oikos.R
import objects.DatosInmueble

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var datosFicha : DatosInmueble
    lateinit var filterCard : CardView
    lateinit var editButton : AppCompatButton
    lateinit var filterSearchButton : AppCompatButton
    lateinit var tipoText : TextView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)

        filterCard = root.findViewById(R.id.filter_search_card)
        filterCard.visibility = View.INVISIBLE

        editButton = root.findViewById(R.id.bEditar)
        editButton.setOnClickListener {
            filterCard.visibility = if (filterCard.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        }

        filterSearchButton = root.findViewById(R.id.button_filter_search)
        val tipoSpinner : AppCompatSpinner = root.findViewById(R.id.filtro_tipo)
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_values,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSpinner.adapter = adapter
        }
        tipoText = root.findViewById(R.id.filter_tipo_text)

        filterSearchButton.setOnClickListener {
            //Aplicar las nuevas preferencias
        }



        //tipoSpinner.onItemSelectedListener = this
       // datosFicha = arguments?.getSerializable("inmueble") as DatosInmueble
       // setData(root, datosFicha)

        userViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it


        })
        return root
    }








    /*
    fun setData(view : View, data: DatosInmueble){
        val ciudad = view.findViewById<TextView>(R.id.tCiudad)
        val tipo = view.findViewById<TextView>(R.id.tTipo)
        val precio  = view.findViewById<TextView>(R.id.tPrecio)
        val habs = view.findViewById<TextView>(R.id.tHabs)
        val superficie = view.findViewById<TextView>(R.id.tSuperficie)
        val garaje = view.findViewById<TextView>(R.id.tGaraje)
        val baños = view.findViewById<TextView>(R.id.tBaño)

        ciudad.text = data.direccion
        tipo.text = data.tipo
        precio.text = "${data.precio}€"
        habs.text =  "${data.habitaciones}"
        superficie.text = "${data.superficie}m\u00B2"
        garaje.text = if(data.garaje) "Sí" else "No"
        baños.text =  "${data.baños}"
    }  */

}