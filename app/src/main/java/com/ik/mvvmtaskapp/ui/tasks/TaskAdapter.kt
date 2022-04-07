package com.ik.mvvmtaskapp.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.databinding.ItemTaskBinding

class TaskAdapter(private val listener:  OnItemClickListener) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
    val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return TaskViewHolder(binding)
  }

  override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
    val currentItem = getItem(position)
    holder.bind(currentItem)
  }

  inner class TaskViewHolder(
    private val binding: ItemTaskBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    init {
      binding.apply {
        root.setOnClickListener {
          val position = adapterPosition
          if (position != RecyclerView.NO_POSITION) {
            val task = getItem(position)
            listener.onItemClick(task)
          }
        }
        checkBoxCompleted.setOnClickListener {
          val position = adapterPosition
          if (position!=RecyclerView.NO_POSITION) {
            val task = getItem(position)
            listener.onCheckBoxClick(task, checkBoxCompleted.isChecked)
          }
        }
      }
    }

    fun bind(task: Task) {
      binding.apply {
        checkBoxCompleted.isChecked = task.checked
        textViewTask.text = task.name
        textViewTask.paint.isStrikeThruText = task.checked
      }
    }
  }

  interface OnItemClickListener {
    fun onItemClick(task: Task)
    fun onCheckBoxClick(task: Task, isChecked: Boolean)
  }

  class DiffCallback: DiffUtil.ItemCallback<Task>() {
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem

    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
  }
}