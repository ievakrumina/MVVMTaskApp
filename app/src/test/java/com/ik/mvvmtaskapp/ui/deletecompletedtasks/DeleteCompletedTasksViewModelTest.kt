package com.ik.mvvmtaskapp.ui.deletecompletedtasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ik.mvvmtaskapp.data.TaskRepository
import com.ik.mvvmtaskapp.ui.tasks.TasksViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeleteCompletedTasksViewModelTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewModel: DeleteCompletedTasksViewModel
  private val testDispatcher = TestCoroutineDispatcher()

  @MockK
  private lateinit var repository: TaskRepository

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    viewModel = DeleteCompletedTasksViewModel(repository)
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