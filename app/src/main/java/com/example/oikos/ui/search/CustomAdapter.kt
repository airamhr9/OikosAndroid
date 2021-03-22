package com.example.oikos.ui.search

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmuebleActivity
import objects.DatosInmueble
import java.net.URL


class CustomAdapter(private val dataSet: ArrayList<DatosInmueble>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inmuebleCardView : CardView = view.findViewById(R.id.inmueble_card)
        val priceText : TextView = view.findViewById(R.id.inmueble_card_price)
        val addressText : TextView = view.findViewById(R.id.inmueble_card_address)
        val tipoTextView : TextView = view.findViewById(R.id.inmueble_card_tipo)
        val tipoCardView : CardView = view.findViewById(R.id.inmueble_card_tipo_card)
        val numImagenes : TextView = view.findViewById(R.id.inmueble_card_num_images)
        val imagen : ImageView = view.findViewById(R.id.inmueble_card_image)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tarjeta_inmueble, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.priceText.text = "${dataSet[position].precio}€"
        viewHolder.addressText.text = dataSet[position].direccion
        viewHolder.tipoTextView.text = dataSet[position].tipo
        if(dataSet[position].tipo == "Alquiler")
            viewHolder.tipoCardView.setCardBackgroundColor(Color.parseColor("#42a5f5"))
        viewHolder.numImagenes.text = "${dataSet[position].images.size} imágenes"

        viewHolder.inmuebleCardView.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, FichaInmuebleActivity :: class.java)
            intent.putExtra("inmueble", dataSet[position])
            viewHolder.itemView.context.startActivity(intent)
        }
        var url = URL(dataSet[position].images.first())
        url = URL("http://10.0.2.2:9000${url.path}")

        Glide.with(viewHolder.itemView).asBitmap().load(url.toString()).into(viewHolder.imagen)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
