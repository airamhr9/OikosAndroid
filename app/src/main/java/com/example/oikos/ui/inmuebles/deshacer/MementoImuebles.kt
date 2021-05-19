package com.example.oikos.ui.inmuebles.deshacer

import com.example.oikos.ui.inmuebles.GestionAdapter
import com.example.oikos.ui.inmuebles.GestionInmuebleFragment
import objects.InmuebleWithModelo

class MementoImuebles (private val inmuebleFragment : GestionInmuebleFragment,
                       private val inmuebleModificado : InmuebleWithModelo,
                       private val visiblelistaInmuebles : ArrayList<InmuebleWithModelo>,
                       private val invisiblelistaInmuebles : ArrayList<InmuebleWithModelo>) : Memento{

    override fun restaurar() {
        inmuebleFragment.setState(visiblelistaInmuebles, invisiblelistaInmuebles, inmuebleModificado)
    }
}