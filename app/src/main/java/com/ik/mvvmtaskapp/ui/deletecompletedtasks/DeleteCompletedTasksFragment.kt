package com.ik.mvvmtaskapp.ui.deletecompletedtasks

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.ik.mvvmtaskapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCompletedTasksFragment : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
      builder.setMessage(R.string.delete_completed_tasks_alert)
        .setPositiveButton(R.string.delete_confirmation) { _, _ ->
          setFragmentResult(
            "delete_tasks_request",
            bundleOf("delete_tasks_result" to true)
          )
        }
        .setNegativeButton(R.string.cancel, null)
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }
}