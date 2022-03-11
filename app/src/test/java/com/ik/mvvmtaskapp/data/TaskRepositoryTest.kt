package com.ik.mvvmtaskapp.data

import com.ik.mvvmtaskapp.ui.tasks.SortOrder
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.*
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
  fun `get tasks`(){
    //val flow = MutableStateFlow(listOf(Task(name = "First task")))
    //every { taskDao.getTasks(any(), any(), any()) } returns flow
    //val result = repo.getTasks("",SortOrder.BY_NAME, false)
    //assertEquals("First task", result.collect { it.first().name })
  }

  @After
  fun tearDown() {
    unmockkAll()
  }
}