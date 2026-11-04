package com.example.studymate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton


class SettingsFragment : Fragment() {

private lateinit var backToHome: Button
    private lateinit var btnDarkModeArrow: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToHome = view.findViewById<Button>(R.id.backToHome)
        btnDarkModeArrow = view.findViewById(R.id.btnDarkModeArrow)


        backToHome.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
        }

        btnDarkModeArrow.setOnClickListener {
            startActivity(Intent(requireContext(), DarkMode::class.java))
        }
    }

}