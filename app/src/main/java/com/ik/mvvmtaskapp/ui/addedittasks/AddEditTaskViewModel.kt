package com.ik.mvvmtaskapp.ui.addedittasks

import androidx.lifecycle.*
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import com.ik.mvvmtaskapp.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
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
    if (handleEmptyTask()) return
    if (task == null) {
      handleCreateTask()
    } else {
      handleUpdateTask()
    }
  }

  private fun handleEmptyTask(): Boolean {
    if (taskName.isBlank()) {
      addEditTaskResult(AddEditTaskState.Invalid(InvalidTask.EMPTY))
      return true
    }
    return false
  }

  private fun handleUpdateTask() {
    val updatedTask = task?.copy(name = taskName)
    updatedTask?.let { updateTask(it) }
  }

  private fun handleCreateTask() {
    val newTask = Task(name = taskName)
    createTask(newTask)
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