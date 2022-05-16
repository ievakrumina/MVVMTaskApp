package com.ik.mvvmtaskapp.ui.tasks.robots

import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.utils.*

object TaskListRobot {

  fun assertToolbarName() = R.id.action_bar.assertHasDescendantTextAtPosition(R.string.app_name)
  fun assertSearchOption() = R.id.action_search.assertVisibility()
  fun assertSortOption() = R.id.action_sort.assertVisibility()
  fun clickOnSearch() = R.id.action_search.clickWithId()

  fun clickOnHideCompleted() = R.id.action_bar.clickOnMenuItem(R.string.hide_completed_tasks)
  fun clickOnDeleteCompleted() = R.id.action_bar.clickOnMenuItem(R.string.delete_completed_tasks)

  fun clickOnSortByName() {
    R.id.action_sort.clickWithId()
    R.string.sort_by_name.clickWithText()
  }

  fun clickOnSortByDate() {
    R.id.action_sort.clickWithId()
    R.string.sort_by_date.clickWithText()
  }
  fun typeSearchQuery(text:String) = R.id.search_src_text.typeText(text)
  fun clearSearchQuery() = R.id.search_close_btn.clickWithId()

  fun assertListEmptyStateIsDisplayed() = R.id.animation_empty_list.assertVisibility()

  fun assertTaskAtPositionIsDisplayed(position: Int) =
    R.id.text_view_task.assertVisibilityAtPosition(position)
  fun assertTaskWithNameIsDisplayed(name: String, position: Int) =
    R.id.text_view_task.assertContainsTextAtPosition(name, position)

  fun clickOnTaskAtPosition(position: Int) = R.id.text_view_task.clickOnItemAt(position)
  fun deleteTaskAtPosition(position: Int) = R.id.text_view_task.swipeToLeftAtPosition(position)
  fun assertDeleteTaskToastIsDisplayed(name: String) = assertSnackBar(name)

  fun clickOnCheckBoxForTaskAtPosition(position: Int) = R.id.check_box_completed.clickOnItemAt(position)
  fun assertTaskIsChecked(position: Int, isChecked: Boolean = true) =
    R.id.check_box_completed.assertCheckBoxIsChecked(position,isChecked)

  fun assertAddTaskButtonIsDisplayed() = R.id.fab_add_task.assertVisibility()
  fun clickOnAddTaskButton() = R.id.fab_add_task.clickWithId()

}