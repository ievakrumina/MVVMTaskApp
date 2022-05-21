package com.ik.mvvmtaskapp.ui.deletecompletedtasks.robots

import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.utils.assertTextVisibility
import com.ik.mvvmtaskapp.utils.clickWithText

object DeleteCompletedRobot {

  fun assertAlertTitleIsDisplayed() = R.string.delete_completed_tasks_alert.assertTextVisibility()
  fun assertCancelButton() = R.string.cancel.assertTextVisibility()
  fun assertConfirmButton() = R.string.delete_confirmation.assertTextVisibility()

  fun clickOnDeleteTasks() = R.string.delete_confirmation.clickWithText()
}