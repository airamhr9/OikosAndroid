package com.example.oikos.ui.inmuebles

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.*
import androidx.cardview.widget.CardView
import com.example.oikos.R
import com.example.oikos.ui.search.SearchResultsActivity
import com.example.oikos.ui.search.localized.LocalizedSearch
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text

class GestionInmuebleFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        */
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_gestion_inmueble, container, false)

        val publishFab = root.findViewById<FloatingActionButton>(R.id.publish_fab)
        publishFab.setOnClickListener {
            val intent = Intent(requireContext(), PublicarAnunciosActivity::class.java)
            startActivity(intent)
        }

        return root
    }

}