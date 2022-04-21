package com.ik.mvvmtaskapp.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import junit.framework.Assert.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class TaskDaoTest {

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var taskDatabase: TaskDatabase
  private lateinit var taskDao: TaskDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    taskDatabase = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    taskDao = taskDatabase.taskDao()
  }

  @Test
  fun insertTaskTest() = runBlockingTest {
    taskDao.insert(Task("First task"))
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false).first()
    assertEquals("First task", result[0].name)
  }

  @Test
  fun deleteTaskTest() = runBlockingTest {
    taskDao.delete(Task("First task"))
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false).first()
    assertTrue(result.isEmpty())
  }

  @Test
  fun updateTaskTest() = runBlockingTest {
    taskDao.insert(Task("First task"))
    taskDao.update(Task("Update task", false, 0,1))
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false).first()
    assertEquals("Update task", result[0].name)
  }

  @Test
  fun deleteCompletedTasksTest() = runBlockingTest{
    taskDao.insert(Task("First task", true))
    taskDao.insert(Task("Second task", true))
    taskDao.insert(Task("Third task", false))
    taskDao.deleteCompletedTasks()
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false).first()
    assertTrue(result.size == 1)
    assertEquals("Third task", result[0].name)
  }

  @After
  fun tearDown() {
    taskDatabase.close()
  }

}