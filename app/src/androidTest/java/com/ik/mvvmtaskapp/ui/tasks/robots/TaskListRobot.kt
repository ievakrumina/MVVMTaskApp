package com.ik.mvvmtaskapp.ui.tasks.robots

import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.utils.*


object TaskListRobot {

  fun assertListEmptyStateIsDisplayed() = R.id.animation_empty_list.assertVisibility()

  fun assertTaskAtPositionIsDisplayed(position: Int) =
    R.id.text_view_task.assertVisibilityAtPosition(position)
  fun clickOnTaskAtPosition(position: Int) = R.id.text_view_task.clickOnItemAt(position)
  fun deleteTaskAtPosition(position: Int) = R.id.text_view_task.swipeToLeftAtPosition(position)

  fun assertAddTaskButtonIsDisplayed() = R.id.fab_add_task.assertVisibility()
  fun clickOnAddTaskButton() = R.id.fab_add_task.clickWithId()

}