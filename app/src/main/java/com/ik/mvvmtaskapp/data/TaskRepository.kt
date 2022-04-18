package com.ik.mvvmtaskapp.data

import com.ik.mvvmtaskapp.di.ApplicationScope
import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "TaskRepository"

class TaskRepository @Inject constructor(
  private val taskDao: TaskDao,
  @ApplicationScope private val applicationScope: CoroutineScope
) {
  private val _tasks =  MutableStateFlow<Resource<List<Task>>>(Resource.Loading())

  // Option 2: Map existing flow to Resource<List<Task>>
  fun getTasks2(query: String,order: SortOrder,hideComplete: Boolean): Flow<Resource<List<Task>>> {
    return taskDao.getTasks(query, order, hideComplete)
        .map { list ->
          Resource.Success(list)
        }
      //How to map error and loading state?
      .catch {  }
  }

  // Option 1: Create a new flow with states
  suspend fun getTasks(query: String, order: SortOrder, hideComplete: Boolean): Flow<Resource<List<Task>>> {
    applicationScope.launch {
      try {
        taskDao.getTasks(query, order, hideComplete).collect {
          //Why doesn't _tasks.emit() work?
          _tasks.value = Resource.Success(it)
        }
      } catch (e: Throwable) {
        //Why doesn't _tasks.emit() work?
        _tasks.value = Resource.Error(e)
      }
    }
    return _tasks
  }

  suspend fun updateTask(task: Task) = taskDao.update(task)

  fun deleteCompletedTasks() {
    applicationScope.launch {
      taskDao.deleteCompletedTasks()
    }
  }

  suspend fun insertTask(task: Task)  = taskDao.insert(task)
}