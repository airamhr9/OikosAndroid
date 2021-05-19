package com.example.oikos.ui.inmuebles.deshacer

interface Originador {
    fun guardar() : Memento
}