package com.ik.mvvmtaskapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.databinding.FragTasksBinding
import com.ik.mvvmtaskapp.ui.addedittasks.TaskAction
import com.ik.mvvmtaskapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.frag_tasks), TaskAdapter.OnItemClickListener {
  private val viewModel: TasksViewModel by viewModels()

  /** Using onCreate instead of viewModel init block.\
   *  Called to do initial creation of a fragment
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.getTasks()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val binding = FragTasksBinding.bind(view)
    val taskAdapter = TaskAdapter(this)

    binding.apply {
      recyclerViewTasks.apply {
        adapter = taskAdapter
        layoutManager = LinearLayoutManager(requireContext())
        setHasFixedSize(true)
      }

      fabAddTask.setOnClickListener { view ->
        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(null, getString(R.string.new_task))
        view.findNavController().navigate(action)
      }
    }

    setFragmentResultListener("add_edit_request") {_, bundle ->
      when(bundle.get("add_edit_result")) {
        TaskAction.CREATED ->
          Snackbar
            .make(requireView(), R.string.task_created, Snackbar.LENGTH_SHORT)
            .show()
        TaskAction.UPDATED ->
          Snackbar
            .make(requireView(), R.string.task_updated, Snackbar.LENGTH_SHORT)
            .show()
      }
    }

    viewModel.tasks.observe(viewLifecycleOwner) { taskState ->
      when (taskState) {
        TasksViewModel.TaskListState.Empty -> {
          binding.linearLayoutLoading.visibility = View.GONE
          binding.linearLayoutNoTasks.visibility = View.VISIBLE
          binding.recyclerViewTasks.visibility = View.GONE
        }
        is TasksViewModel.TaskListState.Success -> {
          binding.linearLayoutLoading.visibility = View.GONE
          binding.linearLayoutNoTasks.visibility = View.GONE
          binding.recyclerViewTasks.visibility = View.VISIBLE
          taskAdapter.submitList(taskState.list)
        }
        is TasksViewModel.TaskListState.Error -> {
          binding.linearLayoutLoading.visibility = View.GONE
          binding.linearLayoutNoTasks.visibility = View.VISIBLE
          binding.recyclerViewTasks.visibility = View.GONE
          Toast.makeText(context,R.string.generic_error, Toast.LENGTH_SHORT).show()
        }
        is TasksViewModel.TaskListState.Loading -> {
          binding.linearLayoutLoading.visibility = View.VISIBLE
          binding.linearLayoutNoTasks.visibility = View.GONE
          binding.recyclerViewTasks.visibility = View.GONE
        }
      }
    }

    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_frag_tasks, menu)

    val searchItem = menu.findItem(R.id.action_search)
    val searchView = searchItem.actionView as SearchView

    searchView.onQueryTextChanged {
      viewModel.searchQueryTasks(it)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_sort_by_name -> {
        viewModel.sortTasksByName()
        true
      }
      R.id.action_sort_by_date_created -> {
        viewModel.sortTasksByDate()
        true
      }
      R.id.action_hide_completed_tasks -> {
        item.isChecked = !item.isChecked
        viewModel.hideCompletedTasks(item.isChecked)
        true
      }
      R.id.action_delete_completed_tasks -> {
        val action = TasksFragmentDirections.actionGlobalDeleteCompletedTasksFragment()
        view?.findNavController()?.navigate(action)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onItemClick(task: Task) {
    val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(task, getString(R.string.edit_task))
    view?.findNavController()?.navigate(action)
  }

  override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
    viewModel.onTaskCheckChanged(task, isChecked)
  }
}
