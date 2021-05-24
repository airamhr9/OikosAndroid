package com.example.oikos.ui.inmuebles

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.bumptech.glide.Glide
import com.example.oikos.LoadUserActivity
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmuebleActivity
import com.example.oikos.ui.inmuebles.deshacer.Originador
import objects.InmuebleWithModelo
import objects.Usuario
import java.net.URL


class GestionAdapter(private var dataSet: ArrayList<InmuebleWithModelo>, val visible: Boolean, val fragment: GestionInmuebleFragment) :
        RecyclerView.Adapter<GestionAdapter.ViewHolder>(), Originador {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inmuebleCardView: CardView = view.findViewById(R.id.inmueble_card)
        val priceText: TextView = view.findViewById(R.id.inmueble_card_price)
        val addressText: TextView = view.findViewById(R.id.inmueble_card_address)
        val tipoTextView: TextView = view.findViewById(R.id.inmueble_card_tipo)
        val tipoCardView: CardView = view.findViewById(R.id.inmueble_card_tipo_card)
        val numImagenes: TextView = view.findViewById(R.id.inmueble_card_num_images)
        val numVisitas : TextView = view.findViewById(R.id.inmueble_card_num_visitas)
        val imagen: ImageView = view.findViewById(R.id.inmueble_card_image)
        val visibilityButton: ImageButton = view.findViewById(R.id.inmueble_card_visible)
        val deleteButton: ImageButton = view.findViewById(R.id.inmueble_card_delete)
        val editButton: ImageButton = view.findViewById(R.id.inmueble_card_edit)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = if (visible) LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.tarjeta_inmueble_gestion, viewGroup, false)
        else LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.tarjeta_inmueble_gestion_no_visible, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        println("ON BIND MODELO IS " + dataSet[position].modelo)
        viewHolder.priceText.text = "${dataSet[position].inmueble.precio}€"
        viewHolder.addressText.text = dataSet[position].inmueble.direccion
        viewHolder.numVisitas.text = dataSet[position].inmueble.contadorVisitas.toString() + " visitas"
        viewHolder.tipoTextView.text = dataSet[position].inmueble.tipo
        if (dataSet[position].inmueble.tipo == "Alquiler")
            viewHolder.tipoCardView.setCardBackgroundColor(Color.parseColor("#42a5f5"))
        viewHolder.numImagenes.text = "${dataSet[position].inmueble.imagenes.size} imágenes"

        viewHolder.inmuebleCardView.setOnClickListener {
            //Arreglar herencia de obtener usuario


            val activity = fragment.requireActivity() as LoadUserActivity

            if(dataSet[position].inmueble.propietario.mail != activity.loadUser().mail ) {
                var id = dataSet[position].inmueble.id.toString()
                println("inmueble id" + id)
                val query = AndroidNetworking.put("http://10.0.2.2:9000/api/visitas/")
                query.addQueryParameter("id", id)

                query.setPriority(Priority.HIGH).build().getAsString(
                    object : StringRequestListener {
                        override fun onResponse(response: String) {
                            println("put visita conseguido")

                        }

                        override fun onError(error: ANError) {
                            println("put visita fallido")
                        }
                    }
                )
            }

            val intent = Intent(viewHolder.itemView.context, FichaInmuebleActivity::class.java)
            intent.putExtra("inmueble", dataSet[position].inmueble)
            intent.putExtra("modelo", dataSet[position].modelo)
            viewHolder.itemView.context.startActivity(intent)
        }
        if (dataSet[position].inmueble.imagenes.isNotEmpty()) {
            var url = URL(dataSet[position].inmueble.imagenes.first())
            url = URL("http://10.0.2.2:9000${url.path}")
            Glide.with(viewHolder.itemView).asBitmap().load(url.toString()).into(viewHolder.imagen)
        }

        viewHolder.deleteButton.setOnClickListener {
            fragment.context?.let { it1 ->
                AlertDialog.Builder(it1)
                        .setIcon(android.R.drawable.ic_menu_search)
                        .setTitle("Eliminar Inmueble")
                        .setMessage("¿Desea eliminar este inmueble?")
                        .setPositiveButton("Si"
                        ) { _, _ -> deleteInmueble(position) }
                        .setNegativeButton("No") { _, _ ->

                        }.show()
            }
        }
        viewHolder.visibilityButton.setOnClickListener {
            fragment.context?.let { it1 ->
                AlertDialog.Builder(it1)
                        .setIcon(android.R.drawable.ic_menu_search)
                        .setTitle("Cambiar visibilidad")
                        .setMessage("¿Desea cambiar la visibilidad del inmueble?")
                        .setPositiveButton("Si"
                        ) { _, _ -> updateVisibility(position) }
                        .setNegativeButton("No") { _, _ ->

                        }.show()
            }
        }

        viewHolder.editButton.setOnClickListener {
            fragment.startEditActivity(dataSet[position])
        }
    }

    private fun deleteInmueble(pos: Int) {
        fragment.deleteInmueble(dataSet[pos], visible)
    }

    private fun updateVisibility(pos: Int) {
        fragment.updateInmueble(dataSet[pos], visible)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


    override fun guardar(): Originador.Memento {
        return MementoImuebles(dataSet)
    }

    private fun setState(mementoImuebles: MementoImuebles) {
        dataSet.clear()
        dataSet.addAll(mementoImuebles.listaInmuebles)
    }

    inner class MementoImuebles(val listaInmuebles: ArrayList<InmuebleWithModelo>) : Originador.Memento {
        override fun restaurar() {
            setState(this)
        }
    }
}