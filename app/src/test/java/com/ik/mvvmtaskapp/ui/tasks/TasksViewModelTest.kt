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
      when(it) {
        is TasksViewModel.TaskListState.Success -> {
          assertEquals("First task", it.list[0].name)
          assertEquals("Second task", it.list[1].name)
        }
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `hide completed tasks`() = runBlockingTest {
    viewModel.hideCompletedTasks(true)
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> {
          assertEquals("First task", it.list[0].name)
          assertEquals("Second task", it.list[1].name)
        }
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `sort tasks by date`() = runBlockingTest {
    viewModel.sortTasksByDate()
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> {
          assertEquals("First task", it.list[0].name)
          assertEquals("Second task", it.list[1].name)
        }
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `sort tasks by name`() = runBlockingTest {
    viewModel.sortTasksByName()
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> {
          assertEquals("First task", it.list[0].name)
          assertEquals("Second task", it.list[1].name)
        }
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `search for existing tasks`() = runBlockingTest {
    viewModel.searchQueryTasks("task")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> {
          assertEquals("First task", it.list[0].name)
          assertEquals("Second task", it.list[1].name)
        }
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> fail()
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `empty list state`() = runBlockingTest {
    coEvery { repository.getTasks(any(), any(), any()) } returns flow { emit(emptyList<Task>()) }
    viewModel.searchQueryTasks("invalid query")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail()
        is TasksViewModel.TaskListState.Error -> fail()
        is TasksViewModel.TaskListState.Empty -> {
          assertEquals(TasksViewModel.TaskListState.Empty, it)
        }
        is TasksViewModel.TaskListState.Loading -> fail()
      }
    }
  }

  @Test
  fun `loading list state`() = runBlockingTest {
    coEvery { repository.getTasks(any(), any(), any()) } returns emptyFlow()
    viewModel.searchQueryTasks("invalid query")
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