package com.ik.mvvmtaskapp.ui.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.data.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

  @MockK
  private lateinit var repository: TaskRepository

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    viewModel = TasksViewModel(repository)
  }

  @Test
  fun `get all tasks`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(listOf(Task("First task")).asSuccess())
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `hide completed tasks`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(listOf(Task("First task")).asSuccess())
    coEvery { repository.getTasks(any(), any(), true) } returns response
    viewModel.hideCompletedTasks(true)
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `sort tasks by date`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(listOf(Task("First task")).asSuccess())
    coEvery { repository.getTasks(any(), SortOrder.BY_DATE, any()) } returns response
    viewModel.sortTasksByDate()
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `sort tasks by name`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(listOf(Task("First task")).asSuccess())
    coEvery { repository.getTasks(any(), SortOrder.BY_NAME, any()) } returns response
    viewModel.sortTasksByName()
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `search for existing tasks`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(listOf(Task("First task")).asSuccess())
    coEvery { repository.getTasks("task", any(), any()) } returns response
    viewModel.searchQueryTasks("task")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `empty list state`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(emptyList<Task>().asSuccess())
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("Invalid query")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> {}
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `list loading state`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(listOf<Task>().asLoading())
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> {}
        }
      }
    }

  @Test
  fun `list error state`() = runBlockingTest {
    val response = MutableStateFlow<Resource<List<Task>>>(listOf<Task>().asError())
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> {}
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `task check changed`() = runBlockingTest {
    //To fix
    coEvery { repository.updateTask(any())} just Runs
    viewModel.onTaskCheckChanged(Task("Task"), true)

  }

  @Test
  fun `delete completed tasks`() = runBlockingTest {
    coEvery {repository.deleteCompletedTasks()} just Runs
    viewModel.deleteCompletedTasks()
  }

  @After
  fun tearDown() {
    unmockkAll()
    Dispatchers.resetMain()
  }
}