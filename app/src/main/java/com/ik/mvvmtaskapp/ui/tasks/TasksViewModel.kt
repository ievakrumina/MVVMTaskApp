package com.ik.mvvmtaskapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ik.mvvmtaskapp.data.Resource
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "TaskViewModel"

class TasksViewModel @ViewModelInject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  sealed class TaskListState {
    object Error : TaskListState()
    object Loading : TaskListState()
    object Empty : TaskListState()
    class Success(val list: List<Task>) : TaskListState()
    class DeleteTask(val task: Task) : TaskListState()
  }

  private val _tasks = MutableLiveData<TaskListState>()
  val tasks: LiveData<TaskListState>
    get() = _tasks

  private val _hideCompletedStatus = MutableStateFlow(false)
  val hideCompletedStatus: StateFlow<Boolean>
    get() = _hideCompletedStatus

  private var searchQuery = ""
  private var sortOrder = SortOrder.BY_NAME
  private var hideCompleted = false

  fun searchQueryTasks(query: String) {
    searchQuery = query
    getTasks()
  }

  fun hideCompletedTasks(checked: Boolean) {
    hideCompleted = checked
    getTasks()
    _hideCompletedStatus.value = checked
  }

  fun sortTasksByDate() {
    sortOrder = SortOrder.BY_DATE
    getTasks()
  }

  fun sortTasksByName() {
    sortOrder = SortOrder.BY_NAME
    getTasks()
  }

  fun getTasks() {
    _tasks.postValue(TaskListState.Loading)
    //Launch IO thread
    viewModelScope.launch {
      //Observe list of tasks from repository
      taskRepository.getTasks(searchQuery, sortOrder, hideCompleted)
        .collect { taskList ->
          when (taskList) {
            is Resource.Error -> _tasks.postValue(TaskListState.Error)
            is Resource.Loading -> _tasks.postValue(TaskListState.Loading)
            is Resource.Success -> {
              when (taskList.data.isEmpty()) {
                true -> _tasks.postValue(TaskListState.Empty)
                //Post value to live data once the list is received
                false -> _tasks.postValue(TaskListState.Success(taskList.data))
              }
            }
          }
        }
    }
  }

  fun onTaskCheckChanged(task: Task, isChecked: Boolean) =
    viewModelScope.launch {
      taskRepository.updateTask(task.copy(checked = isChecked))
    }

  fun onTaskSwiped(task: Task) = viewModelScope.launch {
    _tasks.postValue(TaskListState.DeleteTask(task))
    taskRepository.deleteTask(task)
  }

  fun onUndoDeleteClicked(task: Task) = viewModelScope.launch {
    taskRepository.insertTask(task)
  }

}

enum class SortOrder { BY_NAME, BY_DATE }