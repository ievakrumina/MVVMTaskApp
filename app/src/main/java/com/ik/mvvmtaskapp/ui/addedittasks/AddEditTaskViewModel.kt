package com.ik.mvvmtaskapp.ui.addedittasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
  private val taskRepository: TaskRepository,
  @Assisted private val state: SavedStateHandle
) : ViewModel() {

  sealed class AddEditTaskState {
    data class Invalid(val error: InvalidTask) : AddEditTaskState()
    data class Success(val result: TaskAction) : AddEditTaskState()
  }

  private val _taskUiState = MutableLiveData<AddEditTaskState>()
  val taskUiState: LiveData<AddEditTaskState>
    get() = _taskUiState

  val task = state.get<Task>("task")

  var taskName = state?.get<String>("taskName") ?: task?.name ?: ""
    set(value) {
      field = value
      state.set("taskName", value)
    }

  fun updateTaskName(name: String) {
    taskName = name
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
      val updatedTask = task.copy(name = taskName)
      updateTask(updatedTask)
    }
  }

  private fun addEditTaskResult(taskState: AddEditTaskState) {
    viewModelScope.launch {
      when(taskState) {
        is AddEditTaskState.Success ->
          _taskUiState.postValue(AddEditTaskState.Success(taskState.result))
        is AddEditTaskState.Invalid ->
          _taskUiState.postValue(AddEditTaskState.Invalid(taskState.error))
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