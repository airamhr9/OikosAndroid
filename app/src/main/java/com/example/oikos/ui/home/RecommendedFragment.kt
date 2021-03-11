package com.example.oikos.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmueble
import com.example.oikos.ui.user.UserViewModel
import objects.DatosInmueble

class RecommendedFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_recommended, container, false)
    }

    //TODO(Borrar este método cuando se haya acabado la ficha)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.ficha_button)

        //TODO(mover a tarjeta de inmueble)
        button.setOnClickListener {
            //TODO(Cambiar por petición)
            val temporaryDescription = "Este inmueble se encuentra situado en el centro de Barcelona, tiene una superficie total de 2000 m2 y una superficie útil de 500m2. Está dividido en tres plantas. La planta superior tiene dos habitaciones con armarios empotrados, dos cuartos de baño completos y terraza. La planta inferior tiene una cocina totalmente equipada, salón, comedor y oficina."
            val datosFicha = DatosInmueble(899f, " Calle de Angélica Luis Acosta, 2, 38760 Los Llanos", 2, 3, 105, "Alquiler",
                    temporaryDescription, "Antonio Juan de la Rosa de Guadalupe", "averylongmailtoseeifitfits@gmail.com", true)

            val intent = Intent(this.context, FichaInmueble :: class.java)
            intent.putExtra("inmueble", datosFicha)
            startActivity(intent)
        }
    }

}
