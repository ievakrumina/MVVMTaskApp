package com.ik.mvvmtaskapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.rules.MainDispatcherRule
import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import io.mockk.*
import junit.framework.Assert.*
import kotlinx.coroutines.test.*
import org.junit.*

class TaskRepositoryTest {
  @get:Rule
  val rule = InstantTaskExecutorRule()

  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private lateinit var repo: TaskRepository

  private val taskDao: TaskDao = mockk()

  @Before
  fun setUp() {
    MockKAnnotations.init(this)
    repo = TaskRepository(taskDao)
  }

  @Test
  fun `get tasks successfully`() = runTest {
    val taskList = listOf(Task("First task"))
    coEvery { taskDao.getTasks(any(), any(), any()) } returns taskList
    when(val result = repo.getTasks("", SortOrder.BY_NAME, false)) {
      is Resource.Success -> assertEquals("First task", result.data[0].name)
      is Resource.Error -> fail("Unexpected state")
      is Resource.Loading -> fail("Unexpected state")
    }
  }

  @Test
  fun `update task`() = runTest {
    val task = Task("Test")
    coEvery { taskDao.update(any()) } just Runs
    repo.updateTask(task)
    coVerify { taskDao.update(task) }
  }

  @Test
  fun `insert task`() = runTest {
    val task = Task("Task")
    coEvery { taskDao.insert(any()) } just Runs
    repo.insertTask(task)
    coVerify(exactly = 1) { taskDao.insert(task) }
  }

  @Test
  fun `delete completed tasks`() = runTest {
    coEvery { taskDao.deleteCompletedTasks() } just Runs
    repo.deleteCompletedTasks()
    coVerify(exactly = 1) { taskDao.deleteCompletedTasks()}
  }

  @Test
  fun `delete single task`() = runTest {
    val task = Task("New task")
    coEvery { taskDao.delete(any()) } just Runs
    repo.deleteTask(task)
    coVerify(exactly = 1) { taskDao.delete(task) }
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

}
