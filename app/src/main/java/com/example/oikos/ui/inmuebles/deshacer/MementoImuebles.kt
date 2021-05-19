package com.example.oikos.ui.inmuebles.deshacer

import com.example.oikos.ui.inmuebles.GestionAdapter
import objects.InmuebleModeloFav

class MementoImuebles (val inmuebleAdapter : GestionAdapter, val listaInmuebles : ArrayList<InmuebleModeloFav>) : Memento{
    override fun restaurar() {
    }
}