package com.ik.mvvmtaskapp.ui.addedittasks.robots

import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.utils.*

object AddEditTaskRobot {

    fun assertNewTaskToolbarName() = R.id.action_bar.assertHasDescendantTextAtPosition(R.string.new_task)
    fun assertEditTaskToolbarName() = R.id.action_bar.assertHasDescendantTextAtPosition(R.string.edit_task)

    fun typeText(text: String) = R.id.edit_task_name.typeText(text)
    fun clickOnSaveTask() = R.id.fab_save_task.clickWithId()
    fun assertSaveTaskButtonIsDisplayed() = R.id.fab_save_task.assertVisibility()

    fun assertTaskName(text: String) = R.id.edit_task_name.assertContainsText(text)
    fun assertTaskNameEmpty() = R.id.edit_task_name.assertContainsText("")
    fun assertTaskCreatedDateIsDisplayed(visible: Boolean = true) =
        R.id.text_view_task_created.assertVisibility(parentView = null, visible)

    fun assertInvalidNameToast() = assertSnackBar(R.string.empty_task_error)

}