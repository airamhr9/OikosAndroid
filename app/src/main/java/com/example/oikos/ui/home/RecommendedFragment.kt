package com.example.oikos.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.oikos.R
import com.example.oikos.fichaInmueble.FichaInmueble
import com.example.oikos.ui.user.UserViewModel

class RecommendedFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home_recommended, container, false)
    }

    //TODO(Borrar este método cuando se haya acabado la ficha)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.ficha_button)
        button.setOnClickListener {
            val intent = Intent(this.context, FichaInmueble :: class.java)
            startActivity(intent)
        }
    }

}
