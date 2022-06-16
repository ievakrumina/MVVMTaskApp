package com.ik.mvvmtaskapp.ui.tasks

import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskDao
import com.ik.mvvmtaskapp.data.TaskDatabase
import com.ik.mvvmtaskapp.ui.tasks.robots.TaskListRobot
import com.ik.mvvmtaskapp.utils.UiInteractionUtils
import com.ik.mvvmtaskapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
class TaskFragmentTests {

  @get:Rule(order = 0)
  var hiltRule = HiltAndroidRule(this)

  @Inject
  lateinit var taskDatabase: TaskDatabase
  private lateinit var taskDao: TaskDao

  @Before
  fun setUp() {
    hiltRule.inject()
    taskDao = taskDatabase.taskDao()
  }

  @Test
  fun testTaskListToolbar() {
    launchFragmentInHiltContainer<TasksFragment>()
    TaskListRobot.apply {
      assertToolbarName()
    }
  }

  @Test
  fun testTaskListWithDataState() = runTest {
    val task = Task("First task")
    insertTask(task)
    launchFragmentInHiltContainer<TasksFragment>()
    TaskListRobot.apply {
      assertTaskAtPositionIsDisplayed(position = 0)
      assertAddTaskButtonIsDisplayed()
    }
  }

  @Test
  fun testTaskListEmptyState() {
    launchFragmentInHiltContainer<TasksFragment>()
    TaskListRobot.apply {
      UiInteractionUtils().waitFor(500)
      assertListEmptyStateIsDisplayed()
      assertAddTaskButtonIsDisplayed()
    }
  }

  @Test
  fun testSearchFromToolbar() = runTest {
    val taskOne = Task("First task")
    val taskTwo = Task("Second task")
    insertTask(taskOne)
    insertTask(taskTwo)

    launchFragmentInHiltContainer<TasksFragment>()
    TaskListRobot.apply {
      assertSearchOption()
      clickOnSearch()
      typeSearchQuery("Second")
      assertTaskWithNameIsDisplayed(taskTwo.name, 0)
      clearSearchQuery()
      assertTaskWithNameIsDisplayed(taskOne.name, 0)
    }
  }

  @Test
  fun testSortTasks() = runTest{
    val taskOne = Task("Last task")
    val taskTwo = Task("First task")
    insertTask(taskOne)
    insertTask(taskTwo)

    launchFragmentInHiltContainer<TasksFragment>()
    TaskListRobot.apply {
      assertSortOption()
      clickOnSortByDate()
      assertTaskWithNameIsDisplayed(taskTwo.name,0)
      clickOnSortByName()
      assertTaskWithNameIsDisplayed(taskOne.name, 0)
    }
  }

  @Test
  fun testHideCompletedTasks() = runTest {
    val taskOne = Task("First task")
    insertTask(taskOne)

    launchFragmentInHiltContainer<TasksFragment>{}
    TaskListRobot.apply {
      assertTaskIsChecked(0,false)
      clickOnCheckBoxForTaskAtPosition(0)
      assertTaskIsChecked(0,true)
      clickOnHideCompleted()
      assertListEmptyStateIsDisplayed()
    }
  }

  @Test
  fun testDeleteSingleTask() = runTest {
    lateinit var text: String
    val task = Task("First task")
    insertTask(task)
    launchFragmentInHiltContainer<TasksFragment>{
      text = this.getString(R.string.delete_single_task)
    }
    TaskListRobot.apply {
      deleteTaskAtPosition(0)
      assertDeleteTaskToastIsDisplayed("$text: ${task.name}")
      assertListEmptyStateIsDisplayed()
    }
  }

  @Test
  fun testUndoDeleteSingleTask() = runTest {
    val task = Task("First task")
    insertTask(task)
    launchFragmentInHiltContainer<TasksFragment>()
    TaskListRobot.apply {
      deleteTaskAtPosition(0)
      clickOnUndoDeleteTask()
      assertTaskAtPositionIsDisplayed(0)
    }
  }


  @Test
  fun testDeleteCompletedTasks() {
    val navController = mockk<NavController>(relaxed = true)
    launchFragmentInHiltContainer<TasksFragment>{
      Navigation.setViewNavController(this.requireView(), navController)
    }
    TaskListRobot.apply {
      clickOnDeleteCompleted()
      verify(exactly = 1) {
        navController.navigate(TasksFragmentDirections.actionGlobalDeleteCompletedTasksFragment())
      }
    }
  }


  @Test
  fun testEditTaskNavigation() = runTest {
    lateinit var title: String
    val task = Task("First task")
    insertTask(task)
    val navController = mockk<NavController>(relaxed = true)
    launchFragmentInHiltContainer<TasksFragment>{
      Navigation.setViewNavController(this.requireView(), navController)
      title = this.getString(R.string.edit_task)
    }
    TaskListRobot.apply {
      clickOnTaskAtPosition(0)
      verify(exactly = 1) {
        navController.navigate(TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(title, task.copy(id=1)))
      }
    }
  }


  @Test
  fun testCreateTaskNavigation() {
    lateinit var title: String
    val navController = mockk<NavController>(relaxed = true)

    launchFragmentInHiltContainer<TasksFragment>{
      Navigation.setViewNavController(this.requireView(), navController)
      title = this.getString(R.string.new_task)
    }
    TaskListRobot.apply {
      clickOnAddTaskButton()
      verify(exactly = 1) {
        navController.navigate(TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(title, null))
      }
    }
  }

  @Ignore
  @Test
  fun testReceiveNewTaskData() {
    // TODO
  }

  @Ignore
  @Test
  fun testReceiveEditTaskData() {
    // TODO
  }

  private suspend fun insertTask(task: Task) {
    taskDao.insert(task)
  }
}