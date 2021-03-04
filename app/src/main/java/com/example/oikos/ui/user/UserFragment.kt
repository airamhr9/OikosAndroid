package com.example.oikos.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.oikos.R

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        userViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}