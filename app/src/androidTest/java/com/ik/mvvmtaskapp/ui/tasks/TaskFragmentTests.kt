package com.ik.mvvmtaskapp.ui.tasks

import com.ik.mvvmtaskapp.ui.addedittasks.AddEditTaskFragment
import com.ik.mvvmtaskapp.ui.deletecompletedtasks.DeleteCompletedTasksFragment
import com.ik.mvvmtaskapp.ui.tasks.robots.TaskListRobot
import com.ik.mvvmtaskapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TaskFragmentTests {

  @get:Rule
  var hiltRule = HiltAndroidRule(this)

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  @Test
  fun testTaskListWithDataState() {
    val scenario = launchFragmentInHiltContainer<TasksFragment>{}
    TaskListRobot.apply {
      //Toolbar
      assertTaskAtPositionIsDisplayed(position = 0)
      assertAddTaskButtonIsDisplayed()
    }
  }

  @Test
  fun testTaskListEmptyState() {
    // TODO
  }

  @Test
  fun testTaskListLoadingState() {
    // TODO
  }

  @Test
  fun testTaskListErrorState() {
    // TODO
  }

  @Test
  fun testSearchFromToolbar() {
    // TODO
  }

  @Test
  fun testSortTasks() {
    // TODO
  }

  @Test
  fun testHideCompletedTasks() {
    // TODO
  }
}