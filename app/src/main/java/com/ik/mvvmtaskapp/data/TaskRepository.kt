package com.ik.mvvmtaskapp.data

import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import javax.inject.Inject

class TaskRepository @Inject constructor(
  private val taskDao: TaskDao
) {

  fun getTasks(query: String, order: SortOrder, hideComplete: Boolean) =
    taskDao.getTasks(query, order, hideComplete)

}