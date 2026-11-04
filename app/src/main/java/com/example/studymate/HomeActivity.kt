package com.example.studymate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {
    private lateinit var textviewTaskName: TextView
    private lateinit var textviewStudyMate: TextView
    private lateinit var textviewWelcome : TextView

    private lateinit var textviewDate : TextView

    private lateinit var textviewTodayTitle : TextView

    private lateinit var cardTodayTask : CardView

    private lateinit var textviewTaskDetails : TextView

    private lateinit var textviewUpcomingTitle : TextView

    private lateinit var cardUpcomingTask : CardView

    private lateinit var textviewUpcomingDetails : TextView

    private lateinit var textviewProgressTitle : TextView

    private lateinit var cardProgress : CardView

    private lateinit var textviewCompleted : TextView

    private lateinit var textviewPercentage : TextView

    private lateinit var bottomNav : View

    private lateinit var setting: TextView
    private lateinit var tasks: TextView
    private lateinit var addTask: TextView
    private lateinit var progress: TextView

    private lateinit var focus: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        textviewTaskName= findViewById<TextView>(R.id.textviewTaskName)
        textviewStudyMate= findViewById<TextView>(R.id.textviewStudyMate)
        textviewWelcome = findViewById<TextView>(R.id.textviewWelcome)
        textviewDate = findViewById<TextView>(R.id.textviewDate)
        textviewTodayTitle = findViewById<TextView>(R.id.textviewTodayTitle)
        cardTodayTask = findViewById<CardView>(R.id.cardTodayTask)
        textviewTaskDetails = findViewById<TextView>(R.id.textviewTaskDetails)
        textviewUpcomingTitle  = findViewById<TextView>(R.id.textviewUpcomingTitle)
        cardUpcomingTask  = findViewById<CardView>(R.id.cardUpcomingTask)
        textviewUpcomingDetails  = findViewById<TextView>(R.id.textviewUpcomingDetails)
        textviewProgressTitle = findViewById<TextView>(R.id.textviewProgressTitle)
        cardProgress = findViewById<CardView>(R.id.cardProgress)
        textviewCompleted = findViewById<TextView>(R.id.textviewCompleted)
        textviewPercentage = findViewById<TextView>(R.id.textviewPercentage)
        bottomNav = findViewById<View>(R.id.bottomNav)
        setting = findViewById<TextView>(R.id.setting)
        tasks = findViewById<TextView>(R.id.tasks)
        addTask = findViewById<TextView>(R.id.addTask)
        progress = findViewById<TextView>(R.id.progress)
        focus = findViewById<TextView>(R.id.navFocus)


        setting.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                SettingsFragment()).commit()
        }

        tasks.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                TaskFragment()).commit()
        }

        addTask.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                AddTaskFragment()).commit()
        }

        progress.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                ProgressFragment()).commit()
        }
        focus.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FocusFragment())
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




    }


}