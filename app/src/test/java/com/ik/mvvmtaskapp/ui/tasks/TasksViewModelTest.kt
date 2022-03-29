package com.ik.mvvmtaskapp.ui.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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
    viewModel = TasksViewModel(repository)
  }

  @Test
  fun `get all tasks`() = runBlockingTest {
    val taskFlow = flowOf(listOf(Task("First task")))
    coEvery { repository.getTasks(any(), any(), any()) } returns taskFlow
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `hide completed tasks`() = runBlockingTest {
    val taskFlow = flowOf(listOf(Task("First task", checked = true)))
    coEvery { repository.getTasks(any(), any(), true) } returns taskFlow
    viewModel.hideCompletedTasks(true)
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `sort tasks by date`() = runBlockingTest {
    val taskFlow = flowOf(listOf(Task("First task")))
    coEvery { repository.getTasks(any(), SortOrder.BY_DATE, any()) } returns taskFlow
    viewModel.sortTasksByDate()
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `sort tasks by name`() = runBlockingTest {
    val taskFlow = flowOf(listOf(Task("First task")))
    coEvery { repository.getTasks(any(), SortOrder.BY_NAME, any()) } returns taskFlow
    viewModel.sortTasksByName()
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `search for existing tasks`() = runBlockingTest {
    val taskFlow = flowOf(listOf(Task("First task")))
    coEvery { repository.getTasks("task", any(), any()) } returns taskFlow
    viewModel.searchQueryTasks("task")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `empty list state`() = runBlockingTest {
    coEvery { repository.getTasks(any(), any(), any()) } returns flowOf(emptyList())
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail()
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> assertEquals(TasksViewModel.TaskListState.Empty, it)
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `loading list state`() = runBlockingTest {
    coEvery { repository.getTasks(any(), any(), any()) } returns emptyFlow()
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail()
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> assertEquals(TasksViewModel.TaskListState.Loading, it)
      }
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