package com.ik.mvvmtaskapp.flowtests

import androidx.test.core.app.ActivityScenario
import com.ik.mvvmtaskapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
open class BaseFlowTest {

  @get:Rule
  var hiltRule = HiltAndroidRule(this)

  lateinit var activityScenario: ActivityScenario<MainActivity>

  @Before
  fun setUp() {
    hiltRule.inject()
    activityScenario = ActivityScenario.launch(MainActivity::class.java)
  }

  @After
  fun tearDown() {
    activityScenario.close()
  }
}