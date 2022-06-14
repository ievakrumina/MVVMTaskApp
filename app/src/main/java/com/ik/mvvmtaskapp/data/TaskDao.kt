package com.ik.mvvmtaskapp.data

import androidx.room.*
import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

  suspend fun getTasks(query:String, sortOrder: SortOrder, hideCompleted: Boolean) :List<Task> =
    when (sortOrder) {
      SortOrder.BY_NAME -> getTasksSortedByName(query,hideCompleted)
      SortOrder.BY_DATE -> getTasksSortedByDateCreated(query,hideCompleted)
    }
  @Query("SELECT * from task_table WHERE (checked != :hideCompleted OR checked = 0 ) AND name LIKE '%' || :searchQuery || '%' ORDER BY name")
  suspend fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): List<Task>

  @Query("SELECT * from task_table WHERE (checked != :hideCompleted OR checked = 0 ) AND name LIKE '%' || :searchQuery || '%' ORDER BY created")
  suspend fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): List<Task>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(task: Task)

  @Update
  suspend fun update(task: Task)

  @Delete
  suspend fun delete(task: Task)

  @Query("DELETE FROM task_table WHERE checked == 1")
  suspend fun deleteCompletedTasks()
}