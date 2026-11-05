package com.example.studymate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.firebase.auth.FirebaseAuth
import org.intellij.lang.annotations.Language


class SettingsFragment : Fragment() {


    private lateinit var btnDarkModeArrow: ImageButton

    private lateinit var btnLogout: Button

    private lateinit var multiLangLayout: LinearLayout

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

        btnDarkModeArrow = view.findViewById(R.id.btnDarkModeArrow)

        btnLogout = view.findViewById(R.id.btnLogout)

        multiLangLayout = view.findViewById(R.id.multiLangLayout)


        btnDarkModeArrow.setOnClickListener {
            startActivity(Intent(requireContext(), DarkMode::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }

        multiLangLayout.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                MultiLangFragment()).commit()
        }


    }




}