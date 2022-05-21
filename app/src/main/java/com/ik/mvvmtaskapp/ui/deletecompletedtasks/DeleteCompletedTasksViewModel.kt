package com.ik.mvvmtaskapp.ui.deletecompletedtasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ik.mvvmtaskapp.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteCompletedTasksViewModel@Inject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  fun deleteCompletedTasks() {
    viewModelScope.launch {
      taskRepository.deleteCompletedTasks()
    }
  }
}
