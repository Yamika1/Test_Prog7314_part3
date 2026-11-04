package com.example.studymate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProgressFragment : Fragment() {
    private lateinit var progressCircular: ProgressBar
    private lateinit var textviewCompletionPercentage: TextView
    private lateinit var textviewCompletedOfTotal: TextView
    private lateinit var tvTotalTasks: TextView
    private lateinit var textviewCompletedTasks: TextView
    private lateinit var textviewIncompleteTasks: TextView
    private lateinit var tvOverdueTasks: TextView
    private lateinit var recentTasksContainer: ViewGroup
    private lateinit var backToHome: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_progress, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressCircular = view.findViewById(R.id.progressCircular)
        textviewCompletionPercentage = view.findViewById(R.id.textviewCompletionPercentage)
        textviewCompletedOfTotal = view.findViewById(R.id.textviewCompletedOfTotal)
        tvTotalTasks = view.findViewById(R.id.tvTotalTasks)
        textviewCompletedTasks = view.findViewById(R.id.textviewCompletedTasks)
        textviewIncompleteTasks = view.findViewById(R.id.textviewIncompleteTasks)
        tvOverdueTasks = view.findViewById(R.id.tvOverdueTasks)
        recentTasksContainer = view.findViewById(R.id.recentTasksContainer)

        view.findViewById<TextView>(R.id.navFocus).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FocusFragment())
                .commit()
        }


        backToHome.setOnClickListener {
            startActivity(Intent(requireContext(), HomeActivity::class.java))

        }

        updateDashboard()
    }

    override fun onResume() {
        super.onResume()
        if (::progressCircular.isInitialized) updateDashboard()
    }

    private fun updateDashboard() {
        val tasks = TaskStorage.getTasks(requireContext())
        val total = tasks.size
        val completed = tasks.count { it.completed }
        val incomplete = total - completed
        val overdue = tasks.count { !it.completed && isOverdue(it.dueDate) }
        val percentage = if (total == 0) 0 else ((completed * 100.0) / total).toInt()

        progressCircular.progress = percentage
        textviewCompletionPercentage.text = "$percentage%"
        textviewCompletedOfTotal.text = "$completed of $total tasks completed"
        tvTotalTasks.text = total.toString()
        textviewCompletedTasks.text = completed.toString()
        textviewIncompleteTasks.text = incomplete.toString()
        tvOverdueTasks.text = overdue.toString()

        recentTasksContainer.removeAllViews()
        tasks.sortedByDescending { it.id }.take(5).forEach { task ->
            val card = CardView(requireContext()).apply {
                radius = 16f
                cardElevation = 3f
                setCardBackgroundColor(android.graphics.Color.WHITE)
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 12 }
            }

            val text = TextView(requireContext()).apply {
                text = "${task.name}\n${if (task.completed) "Completed" else "Incomplete"} • Due: ${task.dueDate}"
                textSize = 15f
                setTextColor(android.graphics.Color.BLACK)
                setPadding(20, 20, 20, 20)
            }
            card.addView(text)
            recentTasksContainer.addView(card)
        }
    }

    private fun isOverdue(date: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.isLenient = false
            val due = formatter.parse(date) ?: return false
            val today = formatter.parse(formatter.format(Date())) ?: return false
            due.before(today)
        } catch (_: Exception) {
            false
        }
    }
}