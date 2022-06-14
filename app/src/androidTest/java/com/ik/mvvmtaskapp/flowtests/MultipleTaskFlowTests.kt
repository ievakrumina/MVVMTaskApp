package com.ik.mvvmtaskapp.flowtests

import com.ik.mvvmtaskapp.ui.addedittasks.robots.AddEditTaskRobot
import com.ik.mvvmtaskapp.ui.deletecompletedtasks.robots.DeleteCompletedRobot
import com.ik.mvvmtaskapp.ui.tasks.robots.TaskListRobot
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class MultipleTaskFlowTests: BaseFlowTest() {

  @Test
  fun testMultipleTaskFlow() {
    createTask("Task A")
    createTask("Task B")
    createTask("Task C")

    TaskListRobot.apply {
      clickOnSearch()
      typeSearchQuery("Task C")
      clickOnCheckBoxForTaskAtPosition(0)
      clearSearchQuery()
      assertTaskCount(3)
      clickOnDeleteCompleted()
      DeleteCompletedRobot.clickOnDeleteTasks()
      assertTaskCount(2)
    }
  }

  @Test
  fun testHideTasksFlow() {
    createTask("Task A")
    createTask("Task B")

    TaskListRobot.apply {
      assertTaskCount(2)
      clickOnCheckBoxForTaskAtPosition(0)
      clickOnHideCompleted()
      assertTaskCount(1)
      createTask("Task C")
      assertTaskCount(2)
      clickOnHideCompleted()
      assertTaskCount(3)
    }
  }

  private fun createTask(name: String) {
    TaskListRobot.apply {
      clickOnAddTaskButton()
    }
    AddEditTaskRobot.apply {
      typeText(name)
      clickOnSaveTask()
    }
  }
}