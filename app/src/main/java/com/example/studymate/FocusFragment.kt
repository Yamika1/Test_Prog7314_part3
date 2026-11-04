
package com.example.studymate

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FocusFragment : Fragment() {

    private lateinit var spinnerTasks: Spinner
    private lateinit var timerText: TextView
    private lateinit var sessionType: TextView
    private lateinit var completedSessions: TextView
    private lateinit var totalFocusTime: TextView

    private lateinit var btnFocusSession: Button
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button
    private lateinit var btnShortBreak: Button
    private lateinit var btnLongBreak: Button

    private lateinit var database: FocusSessionDatabase

    private var timer: CountDownTimer? = null

    private val focusDuration = 25 * 60 * 1000L
    private val shortBreakDuration = 5 * 60 * 1000L
    private val longBreakDuration = 15 * 60 * 1000L

    private var timeRemaining = focusDuration
    private var currentBreakDuration = shortBreakDuration

    private var timerRunning = false
    private var isFocusSession = true

    private var currentSessionType = "Focus Session"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_focus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        spinnerTasks = view.findViewById(R.id.spinnerTasks)
        timerText = view.findViewById(R.id.timerText)
        sessionType = view.findViewById(R.id.sessionType)
        completedSessions = view.findViewById(R.id.completedSessions)
        totalFocusTime = view.findViewById(R.id.totalFocusTime)

        btnFocusSession = view.findViewById(R.id.btnFocusSession)
        btnStart = view.findViewById(R.id.btnStart)
        btnPause = view.findViewById(R.id.btnPause)
        btnReset = view.findViewById(R.id.btnReset)
        btnShortBreak = view.findViewById(R.id.btnShortBreak)
        btnLongBreak = view.findViewById(R.id.btnLongBreak)

        // Connect to the Room database
        database = FocusSessionDatabase.getDatabase(requireContext())

        loadTasks()
        observeFocusHistory()
        updateTimerDisplay()

        btnFocusSession.setOnClickListener {
            resetToFocusSession()
        }

        btnStart.setOnClickListener {
            if (isFocusSession && spinnerTasks.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Please select a task first.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startTimer()
            }
        }

        btnPause.setOnClickListener {
            pauseTimer()
        }

        btnReset.setOnClickListener {
            resetTimer()
        }

        btnShortBreak.setOnClickListener {
            startBreak(shortBreakDuration, "Short Break")
        }

        btnLongBreak.setOnClickListener {
            startBreak(longBreakDuration, "Long Break")
        }

        setupNavigation(view)
    }

    // Load incomplete tasks from the existing TaskStorage
    private fun loadTasks() {

        val tasks = TaskStorage.getTasks(requireContext())
            .filter { !it.completed }

        val taskNames = mutableListOf("Select a task")

        taskNames.addAll(tasks.map { it.name })

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            taskNames
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerTasks.adapter = adapter
    }

    private fun observeFocusHistory() {

        database.focusSession().readAllSessions()
            .observe(viewLifecycleOwner) { sessions ->

                completedSessions.text =
                    "Completed sessions: ${sessions.size}"
            }

        database.focusSession().getTotalFocusMinutes()
            .observe(viewLifecycleOwner) { minutes ->

                totalFocusTime.text =
                    "Total focus time: ${minutes ?: 0} minutes"
            }
    }

    private fun startTimer() {

        if (timerRunning) return

        if (timeRemaining <= 0L) {
            timeRemaining = if (isFocusSession) {
                focusDuration
            } else {
                currentBreakDuration
            }
        }

        timerRunning = true
        btnStart.isEnabled = false
        btnPause.isEnabled = true

        timer = object : CountDownTimer(timeRemaining, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                timeRemaining = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {

                timeRemaining = 0L
                timerRunning = false

                btnStart.isEnabled = true
                btnPause.isEnabled = false

                updateTimerDisplay()

                if (isFocusSession) {
                    saveCompletedSession()

                    Toast.makeText(
                        requireContext(),
                        "Focus session completed!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Break completed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun pauseTimer() {

        timer?.cancel()
        timerRunning = false

        btnStart.isEnabled = true
        btnPause.isEnabled = false
    }

    private fun resetTimer() {

        timer?.cancel()
        timerRunning = false

        timeRemaining = if (isFocusSession) {
            focusDuration
        } else {
            currentBreakDuration
        }

        btnStart.isEnabled = true
        btnPause.isEnabled = false

        updateTimerDisplay()
    }
    private fun resetToFocusSession() {

        timer?.cancel()
        timerRunning = false

        isFocusSession = true
        currentSessionType = "Focus Session"
        timeRemaining = focusDuration

        sessionType.text = currentSessionType

        btnStart.isEnabled = true
        btnPause.isEnabled = false

        updateTimerDisplay()
    }
    private fun startBreak(duration: Long, breakName: String) {

        timer?.cancel()
        timerRunning = false

        isFocusSession = false
        currentSessionType = breakName
        currentBreakDuration = duration
        timeRemaining = duration

        sessionType.text = breakName

        btnStart.isEnabled = true
        btnPause.isEnabled = false

        updateTimerDisplay()

        startTimer()
    }
    private fun saveCompletedSession() {

        val selectedTask = spinnerTasks.selectedItem?.toString()
            ?: "Unknown task"

        val currentDate = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        val session = FocusSession(
            taskName = selectedTask,
            durationMinutes = 25,
            sessionDate = currentDate
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            database.focusSession().addSession(session)
        }
    }
    private fun updateTimerDisplay() {

        val minutes = (timeRemaining / 1000) / 60
        val seconds = (timeRemaining / 1000) % 60

        timerText.text = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
    }
    private fun setupNavigation(view: View) {

        view.findViewById<TextView>(R.id.navHome)
            .setOnClickListener {
                startActivity(
                    Intent(requireContext(), HomeActivity::class.java)
                )
                requireActivity().finish()
            }

        view.findViewById<TextView>(R.id.navTasks)
            .setOnClickListener {
                openFragment(TaskFragment())
            }

        view.findViewById<TextView>(R.id.navAddTask)
            .setOnClickListener {
                openFragment(AddTaskFragment())
            }

        view.findViewById<TextView>(R.id.navProgress)
            .setOnClickListener {
                openFragment(ProgressFragment())
            }

        view.findViewById<TextView>(R.id.navSettings)
            .setOnClickListener {
                openFragment(SettingsFragment())
            }
    }

    private fun openFragment(fragment: Fragment) {

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    override fun onDestroyView() {

        timer?.cancel()
        timer = null
        timerRunning = false

        super.onDestroyView()
    }
}