package com.ik.mvvmtaskapp.ui.addedittasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import com.ik.mvvmtaskapp.rules.MainDispatcherRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditTaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val scope = TestScope()

    private lateinit var viewModel: AddEditTaskViewModel

    @MockK
    private lateinit var repository: TaskRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = AddEditTaskViewModel(repository)
    }

    @Test
    fun `error when save new task without name`() = runTest {
        viewModel.onSaveClick()
        viewModel.taskState.observeForever{ state ->
            when (state) {
                is AddEditTaskViewModel.AddEditTaskState.Invalid ->
                    assertEquals(InvalidTask.EMPTY, state.error)
                is AddEditTaskViewModel.AddEditTaskState.Success -> fail("Unexpected state")
            }
        }
    }

    @Test
    fun `error when save existing task without name`() {
        val task = Task("First task")
        viewModel.setCurrentTask(task)
        viewModel.updateTaskName("")
        viewModel.onSaveClick()

        viewModel.taskState.observeForever{ state ->
            when (state) {
                is AddEditTaskViewModel.AddEditTaskState.Invalid ->
                    assertEquals(InvalidTask.EMPTY, state.error)
                is AddEditTaskViewModel.AddEditTaskState.Success -> fail("Unexpected state")
            }
        }
    }

    @Test
    fun `save new task`() = scope.runTest {
        coEvery { repository.insertTask(any()) } just Runs
        viewModel.updateTaskName("First task")
        viewModel.onSaveClick()
        viewModel.taskState.observeForever{ state ->
            when (state) {
                is AddEditTaskViewModel.AddEditTaskState.Invalid -> fail("Unexpected state")
                is AddEditTaskViewModel.AddEditTaskState.Success ->
                    assertEquals(TaskAction.CREATED, state.result)
            }
        }
        coVerify(exactly = 1) { repository.insertTask(any()) }
    }

    @Test
    fun `save updated task`() = runTest {
        val task = Task("First task")
        coEvery { repository.updateTask(any()) } just Runs

        viewModel.setCurrentTask(task)
        viewModel.onSaveClick()

        viewModel.taskState.observeForever{ state ->
            when (state) {
                is AddEditTaskViewModel.AddEditTaskState.Invalid -> fail("Unexpected state")
                is AddEditTaskViewModel.AddEditTaskState.Success ->
                    assertEquals(TaskAction.UPDATED, state.result)
            }
        }

        coVerify(exactly = 1) { repository.updateTask(task) }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}