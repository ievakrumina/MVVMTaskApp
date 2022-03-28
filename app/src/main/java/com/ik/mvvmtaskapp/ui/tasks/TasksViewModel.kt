package com.ik.mvvmtaskapp.ui.tasks

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import com.ik.mvvmtaskapp.ui.tasks.TasksViewModel.TaskListState.Loading
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "TaskViewModel"

class TasksViewModel @ViewModelInject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  sealed class TaskListState {
    object Error : TaskListState()
    object Loading : TaskListState()
    object Empty : TaskListState()
    class Success(val list: List<Task>) : TaskListState()
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
    _tasks.postValue(Loading)
    //Launch IO thread
    viewModelScope.launch {
      //Observe list of tasks from repository
      taskRepository.getTasks(searchQuery, sortOrder, hideCompleted)
        .catch { e->
          if (e is IOException) {
            Log.e(TAG, "Error reading database", e)
            _tasks.postValue(TaskListState.Error)
          } else {
            throw e
          }
        }
        .collect { tasks ->
          when (tasks.isEmpty()) {
            true -> _tasks.postValue(TaskListState.Empty)
            //Post value to live data once the list is received
            false -> _tasks.postValue(TaskListState.Success(tasks))
          }
        }
    }
  }
}

enum class SortOrder { BY_NAME, BY_DATE }