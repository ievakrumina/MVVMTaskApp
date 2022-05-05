package com.ik.mvvmtaskapp.ui.addedittasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import com.ik.mvvmtaskapp.util.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  sealed class AddEditTaskState {
    data class Invalid(val error: InvalidTask) : AddEditTaskState()
    data class Success(val result: TaskAction) : AddEditTaskState()
  }

  private val _taskState = SingleLiveEvent<AddEditTaskState>()
  val taskState: SingleLiveEvent<AddEditTaskState>
    get() = _taskState

  private var task: Task?  = null
  private var taskName = task?.name?: ""

  fun updateTaskName(name: String) {
    taskName = name
  }

  fun setCurrentTask(currentTask: Task?) {
    task = currentTask
    if (task != null) taskName = task?.name.toString()
  }

  fun onSaveClick() {
    if (taskName.isBlank()) {
      addEditTaskResult(AddEditTaskState.Invalid(InvalidTask.EMPTY))
      return
    }

    if (task == null) {
      val newTask = Task(name = taskName)
      createTask(newTask)
    } else {
      val updatedTask = task?.copy(name = taskName)
      updatedTask?.let { updateTask(it) }
    }
  }

  private fun addEditTaskResult(taskState: AddEditTaskState) {
    viewModelScope.launch {
      when(taskState) {
        is AddEditTaskState.Success ->
          _taskState.postValue(AddEditTaskState.Success(taskState.result))
        is AddEditTaskState.Invalid ->
          _taskState.postValue(AddEditTaskState.Invalid(taskState.error))
      }
    }
  }

  private fun updateTask(task: Task) = viewModelScope.launch {
    addEditTaskResult(AddEditTaskState.Success(TaskAction.UPDATED))
    taskRepository.updateTask(task)
  }

  private fun createTask(task: Task) = viewModelScope.launch {
    addEditTaskResult(AddEditTaskState.Success(TaskAction.CREATED))
    taskRepository.insertTask(task)
  }
}

enum class TaskAction {
  CREATED, UPDATED
}

enum class InvalidTask {
  EMPTY
}