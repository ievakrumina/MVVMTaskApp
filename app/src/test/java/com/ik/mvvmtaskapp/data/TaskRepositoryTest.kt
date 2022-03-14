package com.ik.mvvmtaskapp.data

import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {
  private lateinit var repo: TaskRepository

  @MockK
  lateinit var taskDao: TaskDao

  @Before
  fun setUp() {
    MockKAnnotations.init(this)
    repo = TaskRepository(taskDao)
  }

  @Test
  fun `get tasks`() = runBlockingTest {
    val taskFlow = flow {
      emit(listOf(Task("First task")))
    }
    coEvery { taskDao.getTasks(any(), any(), any()) } returns taskFlow
    val result = repo.getTasks("", SortOrder.BY_NAME, false).first()
    assertEquals("First task", result.first().name)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }
}