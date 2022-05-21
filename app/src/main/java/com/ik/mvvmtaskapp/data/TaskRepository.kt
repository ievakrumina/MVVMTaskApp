package com.ik.mvvmtaskapp.data

import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "TaskRepository"

class TaskRepository @Inject constructor(
  private val taskDao: TaskDao
) {

  fun getTasks(query: String,order: SortOrder,hideComplete: Boolean): Flow<Resource<List<Task>>> {
    return taskDao.getTasks(query, order, hideComplete)
      .map { it.asSuccess() as Resource<List<Task>>}
      .catch{emit(Resource.Error(it))}
  }

  suspend fun updateTask(task: Task) = taskDao.update(task)

  suspend fun deleteCompletedTasks()  = taskDao.deleteCompletedTasks()

  suspend fun insertTask(task: Task)  = taskDao.insert(task)

  suspend fun deleteTask(task: Task) = taskDao.delete(task)
}