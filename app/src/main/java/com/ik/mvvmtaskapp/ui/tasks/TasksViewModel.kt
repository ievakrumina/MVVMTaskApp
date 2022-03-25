package com.ik.mvvmtaskapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
  private val taskRepository: TaskRepository,
) : ViewModel() {

  sealed class TaskListState {
    object Error: TaskListState()
    object Loading: TaskListState()
    object Empty: TaskListState()
    class Success(val list: List<Task>): TaskListState()
  }

  private val _tasks = MutableLiveData<TaskListState>()
  val tasks: LiveData<TaskListState>
    get() = _tasks

  private var searchQuery = ""
  private var sortOrder = SortOrder.BY_NAME
  private var hideCompleted = false

  init {
    getTasks()
  }

  fun searchQueryTasks(query: String) {
    searchQuery = query
    getTasks()
  }

  fun hideCompletedTasks(checked: Boolean) {
    hideCompleted = checked
    getTasks()
  }

  fun sortTasksByDate() {
    sortOrder = SortOrder.BY_DATE
    getTasks()
  }

  fun sortTasksByName() {
    sortOrder = SortOrder.BY_NAME
    getTasks()
  }

  private fun getTasks() {
    //Launch IO thread
    viewModelScope.launch {
      //Observe list of tasks from repository
      taskRepository.getTasks(searchQuery, sortOrder, hideCompleted).collect { tasks ->
        when(tasks.isEmpty()) {
          true -> _tasks.postValue(TaskListState.Empty)
          false -> _tasks.postValue(TaskListState.Success(tasks))
        }
        //Post value to live data once the list is received
        //_tasks.postValue(it)
      }
    }
  }
}

enum class SortOrder { BY_NAME, BY_DATE }