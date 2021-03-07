package objects

import com.example.oikos.R

data class DatosInmueble (val price : Float, val direccion : String, val baños : Int, val habitaciones: Int, val superficie : Int, var disponible : Boolean,
                        val descripcion: String, val nombrePropietario : String, val mailPropietario : String, val garaje : Boolean,
                          val images: IntArray = intArrayOf(R.drawable.viewpager1, R.drawable.viewpager2, R.drawable.viewpager3, R.drawable.viewpager4))

