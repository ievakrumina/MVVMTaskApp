package com.ik.mvvmtaskapp.ui.deletecompletedtasks

import androidx.fragment.app.testing.launchFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ik.mvvmtaskapp.ui.deletecompletedtasks.robots.DeleteCompletedRobot
import com.ik.mvvmtaskapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DeleteCompletedFragmentTests {

  @get:Rule(order = 0)
  var hiltRule = HiltAndroidRule(this)

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  @Ignore
  @Test
  fun testDeleteAlertState() {
    // Fix launching fragment for alert
    launchFragment<DeleteCompletedTasksFragment>()
    DeleteCompletedRobot.apply {
      assertAlertTitleIsDisplayed()
      assertCancelButton()
      assertConfirmButton()
    }
  }

  @Ignore
  @Test
  fun testConfirmDeleteNavigation() {
    val navController = mockk<NavController>(relaxed = true)
    // Fix launching fragment for alert
    launchFragmentInHiltContainer<DeleteCompletedTasksFragment>{
      Navigation.setViewNavController(this.requireView(), navController)
    }
    // TODO
  }

  @Ignore
  @Test
  fun testCancelDeleteNavigation() {
    // TODO
  }
}