package com.example.oikos.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oikos.R
import objects.DatosInmueble


class SearchFragment : Fragment() {
    lateinit var searchResults: ArrayList<DatosInmueble>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        searchResults = ArrayList(20)

        //TODO(Borrar esto, inicialización temporal)
        val temporaryDescription = "Este inmueble se encuentra situado en el centro de Barcelona, tiene una superficie total de 2000 m2 y una superficie útil de 500m2. Está dividido en tres plantas. La planta superior tiene dos habitaciones con armarios empotrados, dos cuartos de baño completos y terraza. La planta inferior tiene una cocina totalmente equipada, salón, comedor y oficina."
        var i = 0
        while(i < 20){
            searchResults.add(
                DatosInmueble(
                    899f + i,
                    " Calle de Angélica Luis Acosta, 2, 38760 Los Llanos",
                    i++,
                    3,
                    105,
                    "Alquiler",
                    temporaryDescription,
                    "Antonio Juan de la Rosa de Guadalupe",
                    "averylongmailtoseeifitfits@gmail.com",
                    true
                )
            )
        }

        repeat(20){
            println("AAA AA: ${searchResults[it].baños}")
        }

        val resultsRecycler = root.findViewById<View>(R.id.results_recycler) as RecyclerView
        // TODO(Buscar inmuebles)
        val adapter = CustomAdapter(searchResults)
        resultsRecycler.adapter = adapter
        // Set layout manager to position the items
        resultsRecycler.layoutManager = LinearLayoutManager(context)

        return root
    }


}