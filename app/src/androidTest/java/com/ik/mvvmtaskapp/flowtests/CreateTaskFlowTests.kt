package com.ik.mvvmtaskapp.flowtests

import com.ik.mvvmtaskapp.ui.addedittasks.robots.AddEditTaskRobot
import com.ik.mvvmtaskapp.ui.tasks.robots.TaskListRobot
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class CreateTaskFlowTests: BaseFlowTest() {

  @Test
  fun testSingleTaskFlow() {
    TaskListRobot.apply {
      clickOnAddTaskButton()
    }
    AddEditTaskRobot.apply {
      typeText("First task")
      clickOnSaveTask()
    }
    TaskListRobot.apply {
      assertNewTaskToastIsDisplayed()
      assertTaskAtPositionIsDisplayed(0)
      clickOnTaskAtPosition(0)
    }
    AddEditTaskRobot.apply {
      assertTaskName("First task")
      typeText("New task")
      clickOnSaveTask()
    }
    TaskListRobot.apply {
      assertEditTaskToastIsDisplayed()
      assertTaskAtPositionIsDisplayed(0)
      deleteTaskAtPosition(0)
      assertListEmptyStateIsDisplayed()
    }
  }
}