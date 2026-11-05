package com.example.studymate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat


class MultiLangFragment : Fragment() {
    private lateinit var btnAfrik: Button
    private lateinit var btnZulu: Button
    private lateinit var btnEng: Button
    private lateinit var btnBackToSettings: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multi_lang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAfrik = view.findViewById(R.id.btnAfrik)
        btnZulu = view.findViewById(R.id.btnZulu)
        btnEng = view.findViewById(R.id.btnEng)
        btnBackToSettings = view.findViewById(R.id.btnBackToSettings)

         btnAfrik.setOnClickListener {
             multiLang("af")
         }

        btnEng.setOnClickListener {
            multiLang("en")
        }

        btnZulu.setOnClickListener {
            multiLang("zu")
        }

        btnBackToSettings.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                SettingsFragment()).commit()
        }
    }


    private fun multiLang(language: String) {
        val appLocale= LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }


}