package com.ik.mvvmtaskapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskRepositoryTest {
  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var repo: TaskRepository
  private val testDispatcher = TestCoroutineDispatcher()
  private val testScope = TestCoroutineScope()

  @MockK
  lateinit var taskDao: TaskDao

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    testScope.coroutineContext
    MockKAnnotations.init(this)
    repo = TaskRepository(taskDao)
  }

  @Test
  fun `get tasks successfully`() = runBlockingTest {
    val taskFlow = flowOf(listOf(Task("First task")))
    coEvery { taskDao.getTasks(any(), any(), any()) } returns taskFlow
    when(val result = repo.getTasks("", SortOrder.BY_NAME, false).first()) {
      is Resource.Success -> assertEquals("First task", result.data[0].name)
      is Resource.Loading -> fail("Unexpected state")
      is Resource.Error -> fail("Unexpected state")
    }
  }

  @Test
  fun `get tasks error state`() = runBlockingTest {
    coEvery { taskDao.getTasks(any(), any(), any()) }.throws(Throwable())
    when(repo.getTasks("", SortOrder.BY_NAME, false).first()) {
      is Resource.Success -> fail("Unexpected state")
      is Resource.Loading -> fail("Unexpected state")
      is Resource.Error -> {}
    }
  }

  @After
  fun tearDown() {
    unmockkAll()
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
    testScope.cleanupTestCoroutines()
  }

}
