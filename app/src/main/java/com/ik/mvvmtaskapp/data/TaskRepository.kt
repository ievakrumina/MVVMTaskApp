package com.ik.mvvmtaskapp.data

import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "TaskRepository"

class TaskRepository @Inject constructor(
  private val taskDao: TaskDao
) {

  /**
   * No error catching. Room returns EmptyResultSetException (RuntimeException)
   * Docs: https://developer.android.com/reference/androidx/room/package-summary#exceptions
   */
  suspend fun getTasks(query: String,order: SortOrder,hideComplete: Boolean): Resource<List<Task>> {
    val result =  taskDao.getTasks(query, order, hideComplete)
    return result.asSuccess()
  }

  suspend fun updateTask(task: Task) = taskDao.update(task)

  suspend fun deleteCompletedTasks()  = taskDao.deleteCompletedTasks()

  suspend fun insertTask(task: Task)  = taskDao.insert(task)

  suspend fun deleteTask(task: Task) = taskDao.delete(task)
}