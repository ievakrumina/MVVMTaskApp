package com.ik.mvvmtaskapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ik.mvvmtaskapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor(
  private val taskDao: TaskDao
) : ViewModel() {

  val searchQuery = MutableStateFlow("")
  val sortOder = MutableStateFlow(SortOrder.BY_NAME)
  val hideCompleted = MutableStateFlow(false)

  private val taskFlow = combine(
    searchQuery,
    sortOder,
    hideCompleted
  ) { query, sortOder, hideCompleted ->
    Triple(query, sortOder, hideCompleted)
  }.flatMapLatest { (query, sortOder, hideCompleted) ->
    taskDao.getTasks(query, sortOder, hideCompleted)
  }
  val tasks = taskFlow.asLiveData()
}

enum class SortOrder { BY_NAME, BY_DATE }