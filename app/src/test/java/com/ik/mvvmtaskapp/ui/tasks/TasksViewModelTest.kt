package com.ik.mvvmtaskapp.ui.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: TasksViewModel
  private val testDispatcher = TestCoroutineDispatcher()
  private val testScope = TestCoroutineScope()

  @MockK
  private lateinit var repository: TaskRepository

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    testScope.coroutineContext
    MockKAnnotations.init(this)

    //Mock before view model, because this is used in init block
    val taskFlow = flow { emit(listOf(
      Task("First task"),
      Task("Second task"))) }
    coEvery { repository.getTasks(any(), any(), any()) } returns taskFlow

    viewModel = TasksViewModel(repository)
  }

  @Test
  fun `get all tasks`() = runBlockingTest {
    viewModel.tasks.observeForever {
      assertEquals("First task", it[0].name)
      assertEquals("Second task", it[1].name)
    }
  }

  @Test
  fun `hide completed tasks`() = runBlockingTest {
    viewModel.hideCompletedTasks(true)
    viewModel.tasks.observeForever {
      assertEquals("First task", it[0].name)
      assertEquals("Second task", it[1].name)
    }
  }

  @Test
  fun `sort tasks by date`() = runBlockingTest {
    viewModel.sortTasksByDate()
    viewModel.tasks.observeForever {
      assertEquals("First task", it[0].name)
      assertEquals("Second task", it[1].name)
    }
  }

  @Test
  fun `sort tasks by name`() = runBlockingTest {
    viewModel.sortTasksByName()
    viewModel.tasks.observeForever {
      assertEquals("First task", it[0].name)
      assertEquals("Second task", it[1].name)
    }
  }

  @Test
  fun `search for existing tasks`() = runBlockingTest {
    viewModel.searchQueryTasks("task")
    viewModel.tasks.observeForever {
      assertEquals("First task", it[0].name)
      assertEquals("Second task", it[1].name)
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