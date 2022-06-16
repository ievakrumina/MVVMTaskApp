package com.ik.mvvmtaskapp.ui.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.data.*
import com.ik.mvvmtaskapp.rules.MainDispatcherRule
import io.mockk.*
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksViewModelTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private lateinit var viewModel: TasksViewModel

  private val repository: TaskRepository = mockk()

  @Before
  fun setUp() {
    //Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))
    viewModel = TasksViewModel(repository)
  }

  @Test
  fun `get all tasks`() = runTest {
    val response = listOf(Task("First task")).asSuccess()
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `hide completed tasks`() = runTest {
    val response = listOf(Task("First task")).asSuccess()
    coEvery { repository.getTasks(any(), any(), true) } returns response
    viewModel.hideCompletedTasks(true)
    assertEquals(viewModel.getHideCompletedStatus(), true)

    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `sort tasks by date`() = runTest {
    val response = listOf(Task("First task")).asSuccess()
    coEvery { repository.getTasks(any(), SortOrder.BY_DATE, any()) } returns response
    viewModel.sortTasksByDate()
    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `sort tasks by name`() = runTest {
    val response = listOf(Task("First task")).asSuccess()
    coEvery { repository.getTasks(any(), SortOrder.BY_NAME, any()) } returns response
    viewModel.sortTasksByName()
    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `search for existing tasks`() = runTest {
    val response = listOf(Task("First task")).asSuccess()
    coEvery { repository.getTasks("task", any(), any()) } returns response
    viewModel.searchQueryTasks("task")
    assertEquals(viewModel.getSearchQuery(), "task")
    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> assertEquals("First task", it.list[0].name)
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `empty list state`() = runTest {
    val response = emptyList<Task>().asSuccess()
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("Invalid query")
    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> {}
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `list loading state`() = runTest {
    val response = listOf<Task>().asLoading()
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> {}
      }
    }
  }

  @Test
  fun `list error state`() = runTest {
    val response = listOf<Task>().asError()
    coEvery { repository.getTasks(any(), any(), any()) } returns response
    viewModel.searchQueryTasks("")
    viewModel.tasks.observeForever {
      when (it) {
        is TasksViewModel.TaskListState.Success -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Error -> {}
        is TasksViewModel.TaskListState.Empty -> fail("Unexpected state")
        is TasksViewModel.TaskListState.Loading -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `task check changed`() = runTest {
    val task = Task("Task")
    coEvery { repository.updateTask(any()) } just Runs
    coEvery { repository.getTasks(any(), any(), any())} returns listOf(task).asSuccess()
    viewModel.onTaskCheckChanged(task, true)
    coVerify(exactly = 1) { repository.updateTask(task.copy(checked = true)) }
    coVerify(exactly = 1) {repository.getTasks(any(),any(),any())}
  }

  @Test
  fun `delete task on swipe`() = runTest {
    val task = Task("Task")
    coEvery { repository.deleteTask(any()) } just Runs
    coEvery { repository.getTasks(any(), any(), any())} returns listOf(task).asSuccess()
    viewModel.onTaskSwiped(task)
    viewModel.singleTask.observeForever {
      when (it) {
        is TasksViewModel.SingleTaskState.DeleteTask ->
          assertEquals(task, it.task)
      }
    }
    coVerify(exactly = 1) { repository.deleteTask(task) }
    coVerify(exactly = 1) {repository.getTasks(any(),any(),any())}
  }

  @Test
  fun `undo delete task`() = runTest {
    val task = Task("Task")
    coEvery { repository.insertTask(any()) } just Runs
    coEvery { repository.getTasks(any(), any(), any())} returns listOf(task).asSuccess()
    viewModel.onUndoDeleteClicked(task)
    coVerify(exactly = 1) { repository.insertTask(task) }
    coVerify(exactly = 1) {repository.getTasks(any(),any(),any())}
  }

  @Test
  fun `delete completed tasks`() = runTest {
    val task = Task("Task")
    coEvery { repository.deleteCompletedTasks() } just Runs
    coEvery { repository.getTasks(any(), any(), any())} returns listOf(task).asSuccess()
    viewModel.deleteCompletedTasks()
    coVerify(exactly = 1) { repository.deleteCompletedTasks() }
    coVerify(exactly = 1) {repository.getTasks(any(),any(),any())}
  }

  @After
  fun tearDown() {
    unmockkAll()
  }
}