package com.ik.mvvmtaskapp.ui.tasks

import android.provider.SyncStateContract.Helpers.insert
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ik.mvvmtaskapp.data.TaskRepository
import com.ik.mvvmtaskapp.ui.MainActivity
import com.ik.mvvmtaskapp.ui.tasks.robots.TaskListRobot
import dagger.Provides
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//@HiltAndroidTest
class TaskFragmentTests {

  //@get:Rule(order = 0)
  //var hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  var activityRule: ActivityScenarioRule<MainActivity> =
    ActivityScenarioRule(MainActivity::class.java)


  @Before
  fun setUp() {
    //hiltRule.inject()
  }

  @Test
  fun testTaskList() {
    TaskListRobot.apply {
      assertAddTaskButtonIsDisplayed()
    }
  }


}