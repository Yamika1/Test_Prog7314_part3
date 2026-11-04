package com.example.studymate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.TextView

class AddTaskFragment : Fragment() {

    private lateinit var taskName: EditText
    private lateinit var taskDescription: EditText
    private lateinit var spCategory: Spinner
    private lateinit var spinnerPriority: Spinner
    private lateinit var dueDate: EditText
    private lateinit var btnSaveTask: Button
    private lateinit var backToHome: Button

    private lateinit var taskViewModel: TaskAddViewModel

    private var editingTaskId: Int? = null
    private var existingTask: TaskAdd? = null

    private val categories = arrayListOf(
        "Wellness",
        "Fitness",
        "Academic",
        "Work",
        "Personal"
    )

    private val priorities = arrayListOf(
        "High",
        "Medium",
        "Low"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editingTaskId = arguments?.getInt("taskId")?.takeIf { it != 0 }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_add_task,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel =
            ViewModelProvider(this)[TaskAddViewModel::class.java]

        taskName = view.findViewById(R.id.taskName)
        taskDescription = view.findViewById(R.id.taskDescription)
        dueDate = view.findViewById(R.id.dueDate)
        spCategory = view.findViewById(R.id.spCategory)
        spinnerPriority = view.findViewById(R.id.spinnerPriority)
        btnSaveTask = view.findViewById(R.id.btnSaveTask)
        backToHome = view.findViewById(R.id.backToHome)
        view.findViewById<TextView>(R.id.navFocus).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FocusFragment())
                .commit()
        }

        spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ).apply {
            setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        spinnerPriority.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            priorities
        ).apply {
            setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        dueDate.setOnClickListener {
            showDatePicker()
        }

        editingTaskId?.let { id ->
            taskViewModel.getTaskById(id)
                .observe(viewLifecycleOwner) { task ->

                    if (task != null) {
                        existingTask = task

                        taskName.setText(task.name)
                        taskDescription.setText(task.description)
                        dueDate.setText(task.dueDate)

                        spCategory.setSelection(
                            categories.indexOf(task.category)
                                .coerceAtLeast(0)
                        )

                        spinnerPriority.setSelection(
                            priorities.indexOf(task.priority)
                                .coerceAtLeast(0)
                        )

                        btnSaveTask.text = "Update Task"
                    }
                }
        }

        backToHome.setOnClickListener {
            startActivity(
                Intent(requireContext(), HomeActivity::class.java)
            )
        }

        btnSaveTask.setOnClickListener {
            saveTask()
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }

                val formattedDate = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(selectedDate.time)

                dueDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveTask() {

        val name = taskName.text.toString().trim()
        val description = taskDescription.text.toString().trim()
        val date = dueDate.text.toString().trim()

        val category = spCategory.selectedItem.toString()
        val priority = spinnerPriority.selectedItem.toString()

        if (name.isEmpty()) {
            taskName.error = "Task Name is required"
            taskName.requestFocus()
            return
        }

        if (date.isEmpty()) {
            dueDate.error = "Due Date is required"
            return
        }

        val previousTask = existingTask

        val task = TaskAdd(
            id = previousTask?.id ?: 0,
            name = name,
            description = description,
            category = category,
            priority = priority,
            dueDate = date,
            status = previousTask?.status ?: "Incomplete"
        )

        if (previousTask == null) {
            taskViewModel.addTask(task)
        } else {
            taskViewModel.updateTask(task)
        }

        Toast.makeText(
            requireContext(),
            if (previousTask == null) "Task saved" else "Task updated",
            Toast.LENGTH_SHORT
        ).show()

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView,
                TaskFragment()
            )
            .commit()
    }
}
