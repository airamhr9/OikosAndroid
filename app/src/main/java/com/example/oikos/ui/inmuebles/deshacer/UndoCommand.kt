package com.example.oikos.ui.inmuebles.deshacer

import com.example.oikos.ui.inmuebles.GestionAdapter
import com.example.oikos.ui.inmuebles.GestionInmuebleFragment
import objects.InmuebleWithModelo
import kotlin.reflect.KProperty

class UndoCommand (val gestionFragmment: GestionInmuebleFragment){
    lateinit var mementoImueble : MementoImuebles

    fun guardarInmuebles() {
        mementoImueble = gestionFragmment.guardar() as MementoImuebles
    }

    fun deshacer() {
        if (this::mementoImueble.isInitialized) {
            mementoImueble.restaurar()
        }
    }
}


