package com.ik.mvvmtaskapp.flowtests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class CreateTaskFlowTests {

  @get:Rule
  val activityRule = ActivityScenarioRule(MainActivity::class.java)

  @Test
  fun happyFlowTest() {
    R.id.fab_add_task
  }

}