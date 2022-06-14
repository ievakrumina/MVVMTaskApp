package com.ik.mvvmtaskapp.ui.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.data.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
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
    val response = listOf(Task("First task")).asSuccess()
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
    val response = listOf(Task("First task")).asSuccess()
    coEvery { repository.getTasks(any(), any(), true) } returns response
    viewModel.hideCompletedTasks(true)
    assertEquals(viewModel.getHideCompletedStatus(), true)

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
    val response = listOf(Task("First task")).asSuccess()
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
    val response = listOf(Task("First task")).asSuccess()
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
    val response = listOf(Task("First task")).asSuccess()
    coEvery { repository.getTasks("task", any(), any()) } returns response
    viewModel.searchQueryTasks("task")
    assertEquals(viewModel.getSearchQuery(), "task")

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
    val response = emptyList<Task>().asSuccess()
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
    val response = listOf<Task>().asLoading()
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> { }
      }
      }
    }

  @Test
  fun `list error state`() = runBlockingTest {
    val response = listOf<Task>().asError()
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when(it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> { }
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `task check changed`() = runBlockingTest {
    val task = Task("Task")
    coEvery { repository.updateTask(any())} just Runs
    viewModel.onTaskCheckChanged(task, true)
    coVerify(exactly = 1) { repository.updateTask(task.copy(checked = true)) }
  }

  @Test
  fun `delete task on swipe`() = runBlockingTest {
    val task = Task("Task")
    coEvery { repository.deleteTask(any())} just Runs
    viewModel.onTaskSwiped(task)
    viewModel.singleTask.observeForever {
      when(it) {
        is TasksViewModel.SingleTaskState.DeleteTask ->
          assertEquals(task, it.task)
      }
    }
    coVerify(exactly = 1) { repository.deleteTask(task) }
  }

  @Test
  fun `undo delete task`() = runBlockingTest {
    val task = Task("Task")
    coEvery { repository.insertTask(any()) } just Runs
    viewModel.onUndoDeleteClicked(task)
    coVerify(exactly = 1) { repository.insertTask(task) }
  }

  @After
  fun tearDown() {
    unmockkAll()
    Dispatchers.resetMain()
  }
}