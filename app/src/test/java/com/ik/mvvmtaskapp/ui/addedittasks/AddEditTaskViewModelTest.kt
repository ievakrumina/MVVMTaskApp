package com.ik.mvvmtaskapp.ui.addedittasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditTaskViewModelTest {

  @get:Rule
  val instantExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = TestCoroutineDispatcher()

  @MockK
  private lateinit var repository: TaskRepository

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
  }

  @Test
  fun `get task name from task`() {
    val task = Task("First task")
    val state = SavedStateHandle().apply {
      set("task", task)
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    assertEquals(viewModel.task, task)
    assertEquals(viewModel.taskName, task.name)
  }

  @Test
  fun `get task name from state directly`() {
    val task = Task("First task")
    val state = SavedStateHandle().apply {
      set("task", task)
      set("taskName", "Second task")
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    assertEquals(viewModel.task, task)
    assertEquals(viewModel.taskName, "Second task")
  }

  @Test
  fun `task name is empty when task do not exist`() {
    val state = SavedStateHandle().apply {
      set("task", null)
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    assertEquals(viewModel.task, null)
    assertEquals(viewModel.taskName, "")
  }

  @Test
  fun `update task name`() {
    val name = "New task name"
    val state = SavedStateHandle().apply {
      set("task", null)
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    viewModel.updateTaskName(name)
    assertEquals(viewModel.taskName, name)
  }

  @Test
  fun `save new task without name error`() {
    val state = SavedStateHandle().apply {
      set("task", null)
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    viewModel.onSaveClick()
    viewModel.taskUiState.observeForever {
      when (it) {
        is AddEditTaskViewModel.AddEditTaskState.Invalid ->
          assertEquals(it.error, InvalidTask.EMPTY)
        is AddEditTaskViewModel.AddEditTaskState.Success -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `save existing task without name error`() {
    val state = SavedStateHandle().apply {
      set("task", Task("First task"))
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    viewModel.updateTaskName("")
    viewModel.onSaveClick()
    viewModel.taskUiState.observeForever {
      when (it) {
        is AddEditTaskViewModel.AddEditTaskState.Invalid ->
          assertEquals(it.error, InvalidTask.EMPTY)
        is AddEditTaskViewModel.AddEditTaskState.Success -> fail("Unexpected state")
      }
    }
  }

  @Test
  fun `save new task`() = runBlockingTest {
    val state = SavedStateHandle().apply {
      set("task", null)
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    coEvery { repository.insertTask(any()) } just Runs
    viewModel.updateTaskName("First task")
    viewModel.onSaveClick()
    viewModel.taskUiState.observeForever {
      when (it) {
        is AddEditTaskViewModel.AddEditTaskState.Invalid -> fail("Unexpected state")
        is AddEditTaskViewModel.AddEditTaskState.Success ->
          assertEquals(it.result, TaskAction.CREATED)
      }
    }
    coVerify(exactly = 1) { repository.insertTask(any()) }
  }

  @Test
  fun `save updated task`() = runBlockingTest {
    val task = Task("First task")
    val state = SavedStateHandle().apply {
      set("task", task)
    }
    val viewModel = AddEditTaskViewModel(repository, state)
    coEvery { repository.updateTask(any()) } just Runs
    viewModel.onSaveClick()
    viewModel.taskUiState.observeForever {
      when (it) {
        is AddEditTaskViewModel.AddEditTaskState.Invalid -> fail("Unexpected state")
        is AddEditTaskViewModel.AddEditTaskState.Success ->
          assertEquals(it.result, TaskAction.UPDATED)
      }
    }
    coVerify(exactly = 1) { repository.updateTask(task) }
  }

  @After
  fun tearDown() {
    unmockkAll()
    Dispatchers.resetMain()
  }
}