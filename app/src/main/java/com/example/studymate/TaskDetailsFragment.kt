package com.example.studymate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class TaskDetailsFragment : Fragment() {

    private lateinit var btnBack: Button

    private lateinit var textviewTaskName: TextView
    private lateinit var textviewTaskDescription: TextView
    private lateinit var textviewTaskCategory: TextView
    private lateinit var textviewTaskPriority: TextView
    private lateinit var textviewTaskDueDate: TextView
    private lateinit var textviewTaskStatus: TextView

    private lateinit var btnMarkComplete: Button
    private lateinit var btnEditTask: Button
    private lateinit var btnDeleteTask: Button

    private lateinit var taskViewModel: TaskAddViewModel

    private var taskId: Int = 0
    private var currentTask: TaskAdd? = null
    private var isDeleting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        taskId = when (val value = args?.get("taskId")) {
            is Int -> value
            is Long -> value.toInt()
            else -> 0
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_task_details,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel =
            ViewModelProvider(this)[TaskAddViewModel::class.java]

        btnBack = view.findViewById(R.id.btnBack)

        textviewTaskName =
            view.findViewById(R.id.textviewTaskName)

        textviewTaskDescription =
            view.findViewById(R.id.textviewTaskDescription)

        textviewTaskCategory =
            view.findViewById(R.id.textviewTaskCategory)

        textviewTaskPriority =
            view.findViewById(R.id.textviewTaskPriority)

        textviewTaskDueDate =
            view.findViewById(R.id.textviewTaskDueDate)

        textviewTaskStatus =
            view.findViewById(R.id.textviewTaskStatus)

        btnMarkComplete =
            view.findViewById(R.id.btnMarkComplete)

        btnEditTask =
            view.findViewById(R.id.btnEditTask)

        btnDeleteTask =
            view.findViewById(R.id.btnDeleteTask)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadTask()

        btnMarkComplete.setOnClickListener {
            currentTask?.let { task ->

                val updatedTask = task.copy(
                    status = "Completed"
                )

                taskViewModel.updateTask(updatedTask)

                currentTask = updatedTask
                displayTask(updatedTask)

                Toast.makeText(
                    requireContext(),
                    "Task marked as complete",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnEditTask.setOnClickListener {
            val fragment = AddTaskFragment().apply {
                arguments = Bundle().apply {
                    putInt("taskId", taskId)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    fragment
                )
                .addToBackStack(null)
                .commit()
        }

        // Delete task
        btnDeleteTask.setOnClickListener {
            currentTask?.let { task ->

                isDeleting = true
                taskViewModel.deleteTask(task)

                Toast.makeText(
                    requireContext(),
                    "Task deleted",
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
    }

    private fun loadTask() {

        if (taskId == 0) {
            Toast.makeText(
                requireContext(),
                "Invalid task ID",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.popBackStack()
            return
        }

        taskViewModel.getTaskById(taskId)
            .observe(viewLifecycleOwner) { task ->

                if (task == null) {

                    if (isDeleting) return@observe

                    Toast.makeText(
                        requireContext(),
                        "Task not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    parentFragmentManager.popBackStack()
                    return@observe
                }

                currentTask = task

                displayTask(task)
            }
    }

    private fun displayTask(task: TaskAdd) {

        textviewTaskName.text = task.name

        textviewTaskDescription.text =
            task.description.ifEmpty { "No description" }

        textviewTaskCategory.text = task.category

        textviewTaskPriority.text = task.priority

        textviewTaskDueDate.text = task.dueDate

        textviewTaskStatus.text = task.status

        btnMarkComplete.isEnabled =
            task.status != "Completed"

        btnMarkComplete.text =
            if (task.status == "Completed") {
                "Completed"
            } else {
                "Mark as Complete"
            }
    }
}