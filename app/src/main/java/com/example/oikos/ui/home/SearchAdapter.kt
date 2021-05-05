package com.example.oikos.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmuebleActivity
import com.example.oikos.ui.search.SearchResultsActivity
import com.google.gson.JsonObject
import objects.Busqueda
import objects.InmuebleForList
import java.net.URL

class SearchAdapter(private val dataSet: ArrayList<Busqueda>, val context : Context) :
        RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView : TextView = view.findViewById(R.id.saved_search_name)
        val dateTextView : TextView = view.findViewById(R.id.saved_search_date)
        val filtersTextView : TextView = view.findViewById(R.id.saved_search_filters)
        val filtersButton : LinearLayout = view.findViewById(R.id.saved_search_filters_button)
        val filtersArrow : AppCompatImageView = view.findViewById(R.id.saved_search_arrow)
        val searchButton : AppCompatButton = view.findViewById(R.id.saved_search_search)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.saved_search_card, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.nameTextView.text = dataSet[position].nombre
        viewHolder.dateTextView.text = dataSet[position].fecha
        viewHolder.filtersButton.setOnClickListener {
            changeFiltersVisibility(viewHolder)
        }
        viewHolder.filtersTextView.text = printFilters(dataSet[position].filters)
        viewHolder.searchButton.setOnClickListener {
            val intent = Intent(context, SearchResultsActivity :: class.java)
            val filters = HashMap<String, String>()
            for(key in dataSet[position].filters.keySet())
                filters[key] = dataSet[position].filters[key].asString
            if(filters.isNotEmpty()){
                intent.putExtra("filters", filters)
                context.startActivity(intent)
            }
        }
    }

    private fun changeFiltersVisibility(viewHolder: ViewHolder){
        if(viewHolder.filtersTextView.visibility == View.GONE){
            viewHolder.filtersTextView.visibility = View.VISIBLE
            viewHolder.filtersArrow.animate().rotation(180f).start();
        } else {
            viewHolder.filtersTextView.visibility = View.GONE
            viewHolder.filtersArrow.animate().rotation(0f).start();
        }
    }

    fun printFilters(preferences: JsonObject) : String{
        var filterString = ""
        for (key in preferences.keySet()) {
            when (key) {
                "ciudad" -> filterString += "Ciudad: ${preferences[key].asString}\n"
                "tipo" -> filterString += "Tipo: ${preferences[key].asString}\n"
                "modelo" -> filterString += "Modelo: ${preferences[key].asString}\n"
                "precioMin" -> filterString += "Precio mínimo: ${preferences[key].asString}€\n"
                "precioMax" -> filterString += "Precio máximo: ${preferences[key].asString}€\n"
                "habitaciones" -> filterString += "Habitaciones: ${preferences[key].asString}\n"
                "baños" -> filterString += "Baños: ${preferences[key].asString}\n"
                "supMin" -> filterString += "Superficie mínima: ${preferences[key].asString}m²\n"
                "supMax" -> filterString += "Superficie máxima: ${preferences[key].asString}m²\n"
                "garaje" -> filterString += "Garaje: ${if(preferences[key].asBoolean) "Sí" else "No"}\n"
            }
        }
        return filterString
    }

    override fun getItemCount() = dataSet.size

}
