package com.ik.mvvmtaskapp.flowtests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.ui.MainActivity
import com.ik.mvvmtaskapp.ui.addedittasks.AddEditTaskFragment
import com.ik.mvvmtaskapp.ui.addedittasks.robots.AddEditTaskRobot
import com.ik.mvvmtaskapp.ui.tasks.robots.TaskListRobot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CreateTaskFlowTests {

  @get:Rule(order = 0)
  var hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  val activityRule = ActivityScenarioRule(MainActivity::class.java)

  @Before
  fun setUp() {
    hiltRule.inject()
  }

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
      assertTaskAtPositionIsDisplayed(0)
      clickOnTaskAtPosition(0)
    }
    AddEditTaskRobot.apply {
      assertTaskName("First task")
      typeText("New task")
      clickOnSaveTask()
    }
    TaskListRobot.apply {
      assertTaskAtPositionIsDisplayed(0)
      deleteTaskAtPosition(0)
      assertListEmptyStateIsDisplayed()
    }
  }

}