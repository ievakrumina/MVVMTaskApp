package com.ik.mvvmtaskapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ik.mvvmtaskapp.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  private val searchQuery = MutableStateFlow("")
  private val sortOrder = MutableStateFlow(SortOrder.BY_NAME)
  private val hideCompleted = MutableStateFlow(false)

  fun searchQueryTasks(query: String) {
    searchQuery.value = query
  }

  fun hideCompletedTasks(checked: Boolean) {
    hideCompleted.value = checked
  }

  fun sortTasksByDate() {
    sortOrder.value = SortOrder.BY_DATE
  }

  fun sortTasksByName() {
    sortOrder.value = SortOrder.BY_NAME
  }

  private val taskFlow = combine(
    searchQuery,
    sortOrder,
    hideCompleted
  ) { query, sortOrder, hideCompleted ->
    Triple(query, sortOrder, hideCompleted)
  }.flatMapLatest { (query, sortOrder, hideCompleted) ->
    taskRepository.getTasks(query, sortOrder, hideCompleted)
  }
  val tasks = taskFlow.asLiveData()
}

enum class SortOrder { BY_NAME, BY_DATE }