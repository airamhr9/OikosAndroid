package com.example.oikos.ui.inmuebles.deshacer

import com.example.oikos.ui.inmuebles.GestionAdapter
import com.example.oikos.ui.inmuebles.GestionInmuebleFragment
import objects.InmuebleWithModelo

class MementoImuebles (val inmuebleFragment : GestionInmuebleFragment,
                       val inmuebleModificado : InmuebleWithModelo,
                       val visiblelistaInmuebles : ArrayList<InmuebleWithModelo>,
                       val invisiblelistaInmuebles : ArrayList<InmuebleWithModelo>) : Memento{

    override fun restaurar() {
        inmuebleFragment.setState(visiblelistaInmuebles, invisiblelistaInmuebles, inmuebleModificado)
    }
}