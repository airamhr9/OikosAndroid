package com.example.oikos.ui.home

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import it.sephiroth.android.library.numberpicker.NumberPicker
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import objects.Favorito
import objects.InmuebleModeloFav
import xyz.hanks.library.bang.SmallBangView
import java.net.URL

class FavoritosAdapter(private val dataSet: ArrayList<InmuebleModeloFav>, val fragment: FavoritosFragment) :
    RecyclerView.Adapter<FavoritosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inmuebleCardView : CardView = view.findViewById(R.id.inmueble_card)
        val priceText : TextView = view.findViewById(R.id.inmueble_card_price)
        val addressText : TextView = view.findViewById(R.id.inmueble_card_address)
        val tipoTextView : TextView = view.findViewById(R.id.inmueble_card_tipo)
        val tipoCardView : CardView = view.findViewById(R.id.inmueble_card_tipo_card)
        val numImagenes : TextView = view.findViewById(R.id.inmueble_card_num_images)
        val imagen : ImageView = view.findViewById(R.id.inmueble_card_image)
        val favIcon : SmallBangView = view.findViewById(R.id.fav_image_animation)
        val notaFav : TextView = view.findViewById(R.id.notaFav)
        val editButton : AppCompatImageButton = view.findViewById(R.id.inmueble_fav_edit)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tarjeta_inmueble_fav, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        println("ON BIND MODELO IS " + dataSet[position].modelo)
        viewHolder.priceText.text = "${dataSet[position].inmueble.precio}€"
        viewHolder.addressText.text = dataSet[position].inmueble.direccion
        viewHolder.tipoTextView.text = dataSet[position].inmueble.tipo
        viewHolder.notaFav.text = dataSet[position].nota
        if(dataSet[position].inmueble.tipo == "Alquiler")
            viewHolder.tipoCardView.setCardBackgroundColor(Color.parseColor("#42a5f5"))
        viewHolder.numImagenes.text = "${dataSet[position].inmueble.imagenes.size} imágenes"

        viewHolder.inmuebleCardView.setOnClickListener {
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

        viewHolder.favIcon.isSelected = dataSet[position].esFavorito

        viewHolder.favIcon.setOnClickListener {
            val builder = AlertDialog.Builder(fragment.requireActivity())
            val activity = fragment.requireActivity() as LoadUserActivity
            builder.setMessage("¿Eliminar este inmueble de sus favoritos?")
            builder.setTitle("Eliminar favorito")
                .setPositiveButton(
                        "Eliminar"
                ) { dialog, id ->
                    val query = AndroidNetworking.delete("http://10.0.2.2:9000/api/favorito/")
                    query.addApplicationJsonBody(
                            Favorito(
                                    activity.loadUser(),
                                    dataSet[position].toInmuebleWithModelo(),
                                    "",
                                    0
                            ).toJson()
                    )
                    query.setPriority(Priority.LOW)
                            .build()
                            .getAsString(object : StringRequestListener {
                                override fun onResponse(response: String) {
                                    viewHolder.favIcon.isSelected = false
                                    fragment.eliminarFavorito(position)
                                }

                                override fun onError(error: ANError) {
                                    Snackbar.make(
                                            activity.window.decorView.rootView,
                                            "Error eliminando favorito",
                                            Snackbar.LENGTH_LONG
                                    ).show()
                                    viewHolder.favIcon.isSelected = true
                                }
                            })
                }.setNegativeButton(
                        "Cancelar"
                ) { dialog, id ->
                    dialog.cancel()
                    viewHolder.favIcon.isSelected = true
                }
            builder.show()
        }

        viewHolder.editButton.setOnClickListener {
            val builder = AlertDialog.Builder(fragment.requireActivity())
            val activity = fragment.requireActivity() as LoadUserActivity
            builder.setTitle("Editar favorito")
            val dialogView = activity.layoutInflater.inflate(R.layout.edit_fav_dialog, null)
            builder.setView(dialogView)
            val notasFavoritoTextView = dialogView.findViewById<TextInputEditText>(R.id.notas_fav)
            notasFavoritoTextView.setText(dataSet[position].nota)
            val ordenFavoritoPicker = dialogView.findViewById<NumberPicker>(R.id.fav_numberPicker)
            ordenFavoritoPicker.progress = dataSet[position].orden
            var orden: Int
            ordenFavoritoPicker.doOnProgressChanged { numberPicker, progress, formUser ->
                orden = progress
                println("ORDEN IS $orden")
            }
            builder.setPositiveButton(
                            "Editar"
                    ) { dialog, id ->
                val query = AndroidNetworking.put("http://10.0.2.2:9000/api/favorito/")
                ordenFavoritoPicker.clearFocus()
                val editedFav = Favorito(
                    (fragment.activity as LoadUserActivity).loadUser(),
                    dataSet[position].toInmuebleWithModelo(),
                    notasFavoritoTextView.text.toString(),
                    ordenFavoritoPicker.progress
                )
                query.addApplicationJsonBody(
                    editedFav.toJson()
                )
                query.setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(object : StringRequestListener {
                        override fun onResponse(response: String) {
                            dataSet[position] = InmuebleModeloFav(
                                dataSet[position].inmueble,
                                dataSet[position].modelo,
                                true,
                                editedFav.notas,
                                editedFav.orden)
                            dataSet.sortBy { it.orden }
                            notifyDataSetChanged()
                            Snackbar.make(
                                activity.window.decorView.rootView,
                                "Favorito actualizado",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        override fun onError(error: ANError) {
                            Snackbar.make(
                                activity.window.decorView.rootView,
                                "Error añadiendo favorito",
                                Snackbar.LENGTH_LONG
                            ).show()
                            viewHolder.favIcon.isSelected = false
                        }
                    })
            }
                    .setNegativeButton(
                            "Cancelar"
                    ) { dialog, id ->
                        dialog.cancel()
                    }
            builder.show()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
