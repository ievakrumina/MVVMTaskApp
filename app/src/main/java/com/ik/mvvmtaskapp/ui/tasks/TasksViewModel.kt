package com.ik.mvvmtaskapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  private val _tasks = MutableLiveData<List<Task>>()
  val tasks: LiveData<List<Task>>
    get() = _tasks

  private var searchQuery = "A"
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
      taskRepository.getTasks(searchQuery,sortOrder,hideCompleted).collect {
        //Post value to live data once the list is received
        _tasks.postValue(it)
      }
    }
  }
}

enum class SortOrder { BY_NAME, BY_DATE }