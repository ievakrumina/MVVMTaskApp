package com.ik.mvvmtaskapp.data

import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

private const val TAG = "TaskRepository"

class TaskRepository @Inject constructor(
  private val taskDao: TaskDao
) {
  private val _tasks =  MutableStateFlow<Resource<List<Task>>>(Resource.Loading())

  suspend fun getTasks(query: String, order: SortOrder, hideComplete: Boolean): Flow<Resource<List<Task>>> {
    CoroutineScope(coroutineContext).launch {
      try {
        taskDao.getTasks(query, order, hideComplete).collect {
          _tasks.value = Resource.Success(it)
        }
      } catch (e: Throwable) {
        _tasks.value = Resource.Error(e)
      }
    }
    return _tasks
  }
}