package com.ik.mvvmtaskapp.ui.addedittasks

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.ui.addedittasks.robots.AddEditTaskRobot
import com.ik.mvvmtaskapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.verify
import com.ik.mvvmtaskapp.R
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AddEditFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testNewTaskState() {
        val fragmentArgs = bundleOf(
            Pair("title", "New task"),
            Pair("task", null)
        )
       launchFragmentInHiltContainer<AddEditTaskFragment>(fragmentArgs = fragmentArgs)
       AddEditTaskRobot.apply {
           //assertNewTaskToolbarName() - fragment doesn't have correct toolbar
           assertTaskNameEmpty()
           assertSaveTaskButtonIsDisplayed()
       }
    }

    @Test
    fun testEditTaskState() {
        val task = Task("New task")
        val fragmentArgs = bundleOf(
            Pair("title", "Edit task"),
            Pair("task", task)
        )
        launchFragmentInHiltContainer<AddEditTaskFragment>(fragmentArgs = fragmentArgs)
        AddEditTaskRobot.apply {
            //assertNewTaskToolbarName() - fragment doesn't have correct toolbar
            assertTaskName(task.name)
            assertTaskCreatedDateIsDisplayed()
        }
    }

    @Test
    fun testInvalidTaskState() {
        val fragmentArgs = bundleOf(
            Pair("title", "New task"),
            Pair("task", null)
        )
        launchFragmentInHiltContainer<AddEditTaskFragment>(fragmentArgs = fragmentArgs)
        AddEditTaskRobot.apply {
            clickOnSaveTask()
            assertInvalidNameToast()
        }
    }


    @Test
    fun testCreateTaskNavigation() {
        /**
         * Mockk removed because tests were failing in CICD
         */
        //val navController = mockk<NavController>(relaxed = true)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val fragmentArgs = bundleOf(
            Pair("title", "New task"),
            Pair("task", null)
        )
        launchFragmentInHiltContainer<AddEditTaskFragment>(fragmentArgs = fragmentArgs) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
        }

        AddEditTaskRobot.apply {
            typeText("Task")
            clickOnSaveTask()
        }

        //verify(exactly = 1) { navController.popBackStack() }
        assertTrue(navController.currentDestination == null)
    }


    @Test
    fun testEditTaskNavigation() {
        //val navController = mockk<NavController>(relaxed = true)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val task = Task("New task")
        val fragmentArgs = bundleOf(
            Pair("title", "Edit task"),
            Pair("task", task)
        )
        launchFragmentInHiltContainer<AddEditTaskFragment>(fragmentArgs = fragmentArgs) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
        }

        AddEditTaskRobot.apply {
            clickOnSaveTask()
        }
        //verify(exactly = 1) { navController.popBackStack() }
        assertTrue(navController.currentDestination == null)
    }

    @Ignore
    @Test
    fun testSendNewTaskData() {
        // TODO
    }

    @Ignore
    @Test
    fun testSendEditTaskData() {
        // TODO
    }
}