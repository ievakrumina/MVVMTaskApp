package com.ik.mvvmtaskapp.ui.deletecompletedtasks

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ik.mvvmtaskapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCompletedTasksFragment: DialogFragment() {

  private val viewModel: DeleteCompletedTasksViewModel by viewModels()

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
      builder.setMessage(R.string.delete_completed_tasks_alert)
        .setPositiveButton(R.string.delete_confirmation) { _, _ ->
            viewModel.deleteCompletedTasks()
          }
        .setNegativeButton(R.string.cancel, null)
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }
}