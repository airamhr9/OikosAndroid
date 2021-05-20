package com.example.oikos.ui.inmuebles.deshacer

import com.example.oikos.ui.inmuebles.GestionAdapter
import com.example.oikos.ui.inmuebles.GestionInmuebleFragment
import objects.InmuebleWithModelo
import kotlin.reflect.KProperty

class UndoCommand (private val gestionFragmment: GestionInmuebleFragment){
    private lateinit var mementoImueble : GestionInmuebleFragment.MementoImuebles

    fun guardarInmuebles() {
        mementoImueble = gestionFragmment.guardar() as GestionInmuebleFragment.MementoImuebles
    }

    fun deshacer() {
        if (this::mementoImueble.isInitialized) {
            mementoImueble.restaurar()
        }
    }
}


