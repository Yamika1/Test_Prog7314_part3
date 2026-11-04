package com.example.studymate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskFragment : Fragment() {

    private lateinit var searchTasks: EditText
    private lateinit var filterAll: Button
    private lateinit var filterAssignments: Button
    private lateinit var filterTests: Button
    private lateinit var filterExams: Button
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var emptyTaskMessage: TextView
    private lateinit var backToHome: Button
    private lateinit var adapter: TaskAdapter
    private lateinit var taskViewModel: TaskAddViewModel

    private var selectedFilter = "All"
    private var allTasks: List<TaskAdd> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(this)[TaskAddViewModel::class.java]

        searchTasks = view.findViewById(R.id.searchTasks)
        filterAll = view.findViewById(R.id.filterAll)
        filterAssignments = view.findViewById(R.id.filterAssignments)
        filterTests = view.findViewById(R.id.filterTests)
        filterExams = view.findViewById(R.id.filterExams)
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView)
        emptyTaskMessage = view.findViewById(R.id.emptyTaskMessage)
        backToHome = view.findViewById(R.id.backToHome)

        adapter = TaskAdapter(emptyList()) { task ->
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    TaskDetailsFragment().apply {
                        arguments = Bundle().apply {
                            putInt("taskId", task.id)
                        }
                    }
                )
                .addToBackStack(null)
                .commit()
        }

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        view.findViewById<TextView>(R.id.navFocus).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FocusFragment())
                .commit()
        }

        filterAll.setOnClickListener {
            selectedFilter = "All"
            refreshTasks()
        }

        filterAssignments.setOnClickListener {
            selectedFilter = "Academic"
            refreshTasks()
        }

        filterTests.setOnClickListener {
            selectedFilter = "Tests"
            refreshTasks()
        }

        filterExams.setOnClickListener {
            selectedFilter = "Exams"
            refreshTasks()
        }

        searchTasks.setOnEditorActionListener { _, _, _ ->
            refreshTasks()
            false
        }

        searchTasks.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                refreshTasks()
            }

            override fun afterTextChanged(s: android.text.Editable?) = Unit
        })

        backToHome.setOnClickListener {
            startActivity(Intent(requireContext(), HomeActivity::class.java))
        }

        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            allTasks = tasks
            refreshTasks()
        }
    }

    private fun refreshTasks() {
        val query = searchTasks.text.toString().trim().lowercase()

        val filtered = allTasks.filter { task ->
            val matchesSearch = query.isEmpty() ||
                    task.name.lowercase().contains(query) ||
                    task.description.lowercase().contains(query)

            val matchesFilter = when (selectedFilter) {
                "Academic" -> task.category == "Academic"
                "Tests" -> task.name.contains("test", true) || task.description.contains("test", true)
                "Exams" -> task.name.contains("exam", true) || task.description.contains("exam", true)
                else -> true
            }

            matchesSearch && matchesFilter
        }

        adapter.updateTasks(filtered)
        emptyTaskMessage.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }
}