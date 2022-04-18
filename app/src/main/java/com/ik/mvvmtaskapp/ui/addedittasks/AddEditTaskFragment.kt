package com.ik.mvvmtaskapp.ui.addedittasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.databinding.FragAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.frag_add_edit_task) {

  private val viewModel: AddEditTaskViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val binding = FragAddEditTaskBinding.bind(view)
    binding.apply {
      editTaskName.setText(viewModel.taskName)
      textViewTaskCreated.isVisible = viewModel.task != null
      textViewTaskCreated.text =
        "${getString(R.string.task_created_date)} ${viewModel.task?.createdDateFormatted}"

      editTaskName.addTextChangedListener { name ->
        viewModel.updateTaskName(name.toString())
      }

      fabSaveTask.setOnClickListener {
        viewModel.onSaveClick()
      }
    }

    viewModel.taskUiState.observe(viewLifecycleOwner) { taskState ->
      when (taskState) {
        is AddEditTaskViewModel.AddEditTaskState.Invalid -> {
          when(taskState.error) {
            InvalidTask.EMPTY ->
              Snackbar.make(requireView(), R.string.empty_task_error, Snackbar.LENGTH_SHORT).show()
          }
        }
        is AddEditTaskViewModel.AddEditTaskState.Success -> {
          binding.editTaskName.clearFocus()
          setFragmentResult(
            "add_edit_request",
            bundleOf("add_edit_result" to taskState.result)
          )
          findNavController().popBackStack()
        }
      }
    }
  }
}