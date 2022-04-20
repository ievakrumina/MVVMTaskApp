package com.ik.mvvmtaskapp.ui.deletecompletedtasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ik.mvvmtaskapp.data.TaskRepository
import kotlinx.coroutines.launch

class DeleteCompletedTasksViewModel@ViewModelInject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  fun deleteCompletedTasks() {
    viewModelScope.launch {
      taskRepository.deleteCompletedTasks()
    }
  }
}
