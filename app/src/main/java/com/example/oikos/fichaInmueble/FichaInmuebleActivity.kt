package com.example.oikos.fichaInmueble

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.oikos.R
import com.example.oikos.ui.search.FichaMapFragment
import com.google.android.material.snackbar.Snackbar
import objects.DatosInmueble


class FichaInmuebleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ficha_container_view)
        supportActionBar?.hide()

        if(savedInstanceState == null){
            val datosFicha = intent.getSerializableExtra("inmueble") as DatosInmueble
            val modelo = intent.getStringExtra("modelo") as String
            val bundle = Bundle()
            bundle.putSerializable("inmueble", datosFicha)
            bundle.putString("modelo", modelo)
            val fichaMainFragment: Fragment = FichaInmuebleFragment()
            fichaMainFragment.arguments = bundle
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.ficha_host_fragment,  fichaMainFragment).commit();
        }
    }


   fun changeToMapFragment(view : View){
        val mapFragment: Fragment = FichaMapFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        val datosFicha = intent.getSerializableExtra("inmueble") as DatosInmueble
        bundle.putSerializable("inmueble", datosFicha)
        mapFragment.arguments = bundle

        transaction.replace(R.id.ficha_host_fragment, mapFragment)
        transaction.addToBackStack(null)

        transaction.commit()
    }

    fun sendMail(view: View){
        val datosFicha = intent.getSerializableExtra("inmueble") as DatosInmueble
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(datosFicha.propietario.mail))
        i.putExtra(Intent.EXTRA_SUBJECT, "Contacto por el piso de ${datosFicha.direccion}")
        try {
            startActivity(Intent.createChooser(i, "Enviar correo..."))
        } catch (ex: ActivityNotFoundException) {
            Snackbar.make(view, "No hay clientes de correo instalados", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun onBackPressed (view : View) {
        super.onBackPressed()
    }

}