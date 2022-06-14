package com.ik.mvvmtaskapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
class TaskDaoTest {

  @get:Rule(order = 0)
  var hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @Inject
  @Named("test_db")
  lateinit var taskDatabase: TaskDatabase

  private lateinit var taskDao: TaskDao

  @Before
  fun setUp() {
    hiltRule.inject()
    taskDao = taskDatabase.taskDao()
  }

  @Test
  fun insertTaskTest() = runTest {
    taskDao.insert(Task("First task"))
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false)
    assertEquals("First task", result[0].name)
  }

  @Test
  fun deleteTaskTest() = runTest {
    taskDao.delete(Task("First task"))
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false)
    assertTrue(result.isEmpty())
  }

  @Test
  fun updateTaskTest() = runTest {
    taskDao.insert(Task("First task"))
    taskDao.update(Task("Update task", false, 0,1))
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false)
    assertEquals("Update task", result[0].name)
  }

  @Test
  fun deleteCompletedTasksTest() = runTest {
    taskDao.insert(Task("First task", true))
    taskDao.insert(Task("Second task", true))
    taskDao.insert(Task("Third task", false))
    taskDao.deleteCompletedTasks()
    val result = taskDao.getTasks("", SortOrder.BY_NAME, false)
    assertTrue(result.size == 1)
    assertEquals("Third task", result[0].name)
  }

  @After
  fun tearDown() {
    taskDatabase.close()
  }

}