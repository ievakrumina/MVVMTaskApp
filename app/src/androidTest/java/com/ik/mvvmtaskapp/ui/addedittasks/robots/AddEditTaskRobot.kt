package com.ik.mvvmtaskapp.ui.addedittasks.robots

import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.utils.assertContainsText
import com.ik.mvvmtaskapp.utils.clickWithId
import com.ik.mvvmtaskapp.utils.typeText

object AddEditTaskRobot {

    fun typeText(text: String) = R.id.edit_task_name.typeText(text)
    fun clickOnSaveTask() = R.id.fab_save_task.clickWithId()

    fun assertTaskName(text: String) = R.id.edit_task_name.assertContainsText(text)

}