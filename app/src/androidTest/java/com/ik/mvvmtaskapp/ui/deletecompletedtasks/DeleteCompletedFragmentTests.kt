package com.ik.mvvmtaskapp.ui.deletecompletedtasks

import androidx.fragment.app.testing.launchFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.ui.deletecompletedtasks.robots.DeleteCompletedRobot
import com.ik.mvvmtaskapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.Assert.assertEquals
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
    //val navController = mockk<NavController>(relaxed = true)
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    // Fix launching fragment for alert
    launchFragmentInHiltContainer<DeleteCompletedTasksFragment>{
      navController.setGraph(R.navigation.nav_graph)
      Navigation.setViewNavController(this.requireView(), navController)
    }
    DeleteCompletedRobot.apply {
      clickOnDeleteTasks()
      assertEquals("", navController.currentDestination?.id)
    }
  }

  @Ignore
  @Test
  fun testCancelDeleteNavigation() {
    // TODO
  }
}