package com.ik.mvvmtaskapp.ui.tasks

import com.ik.mvvmtaskapp.data.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class TasksViewModelTest {

  private lateinit var viewModel: TasksViewModel

  @MockK
  lateinit var repository: TaskRepository

  @Before
  fun setUp() {
    MockKAnnotations.init(this)
    viewModel = TasksViewModel(repository)
  }

  @Test
  fun `get all tasks`() {
    //TODO
    //insert some tasks
    //assert that all tasks are returned
  }

  @Test
  fun `hide completed tasks`() {
    //TODO
    //insert some tasks
    //hide completed tasks
    //verify that only not completed tasks are returned
  }

  @Test
  fun `sort tasks by date`() {
    //TODO
    //insert some tasks
    //sort tasks by date
    //verify order
  }

  @Test
  fun `sort tasks by name`() {
    //TODO
    //insert some tasks
    //sort tasks by name
    //verify order
  }

  @Test
  fun `search for existing tasks`() {
    //TODO
    //insert some tasks
    //search by specific name
    //verify correct tasks are returned
  }

  @Test
  fun `search for not existing task`() {
    //TODO
    //insert some tasks
    //search for invalid task name
    //verify empty list is returned
  }

  @Test
  fun `get tasks by using hide completed options, search, sort`() {
    //TODO
    //insert some tasks
    //hide completed
    //search on name
    //sort on name
    //verify correct tasks are returned
  }
}