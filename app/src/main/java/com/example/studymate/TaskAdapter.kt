package com.example.studymate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<TaskAdd>,
    private val onTaskClick: (TaskAdd) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.itemTaskName)
        val details: TextView = view.findViewById(R.id.itemTaskDetails)
        val status: TextView = view.findViewById(R.id.itemTaskStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.name.text = task.name
        holder.details.text = "${task.category} • ${task.priority} • Due: ${task.dueDate}"
        holder.status.text = task.status
        holder.itemView.setOnClickListener { onTaskClick(task) }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<TaskAdd>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}