package com.ik.mvvmtaskapp.ui.tasks

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.databinding.FragTasksBinding
import com.ik.mvvmtaskapp.ui.addedittasks.TaskAction
import com.ik.mvvmtaskapp.ui.deletecompletedtasks.DeleteCompletedTasksFragment
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

  /**
   * Retrieve latest list of tasks after returning to TaskFragment
   */
  override fun onResume() {
    super.onResume()
    Log.d("TaskFrag", "On resume")
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

      swipeListItem(taskAdapter)
      clickOnFabButton()
    }

    actionAfterCreateEditTask()
    actionAfterDeletingCompletedTasks()

    observeTaskList(binding, taskAdapter)
    observeSingleTaskAction()
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_frag_tasks, menu)

    val searchItem = menu.findItem(R.id.action_search)
    var searchView = searchItem.actionView as SearchView

    val pendingQuery = viewModel.getSearchQuery()
    if (pendingQuery.isNotEmpty()) {
      searchItem.expandActionView()
      searchView.setQuery(pendingQuery, false)
    }

    searchView.onQueryTextChanged {
      viewModel.searchQueryTasks(it)
    }

    menu.findItem(R.id.action_hide_completed_tasks).isChecked = viewModel.getHideCompletedStatus()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_sort_by_name -> {
        sortByNameAction()
      }
      R.id.action_sort_by_date_created -> {
        sortByDateAction()
      }
      R.id.action_hide_completed_tasks -> {
        hideCompletedAction(item)
      }
      R.id.action_delete_completed_tasks -> {
        deleteCompletedAction()
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onItemClick(task: Task) {
    val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
      getString(R.string.edit_task),
      task
    )
    view?.findNavController()?.navigate(action)
  }

  override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
    viewModel.onTaskCheckChanged(task, isChecked)
  }


  private fun observeTaskList(
    binding: FragTasksBinding,
    taskAdapter: TaskAdapter
  ) {
    viewModel.tasks.observe(viewLifecycleOwner) { taskState ->
      when (taskState) {
        TasksViewModel.TaskListState.Empty -> {
          emptyListState(binding, taskAdapter)
        }
        is TasksViewModel.TaskListState.Success -> {
          successfulListState(binding, taskAdapter, taskState)
        }
        is TasksViewModel.TaskListState.Error -> {
          errorListState(binding)
        }
        is TasksViewModel.TaskListState.Loading -> {
          loadingListState(binding)
        }
      }
    }
  }

  private fun observeSingleTaskAction() {
    viewModel.singleTask.observe(viewLifecycleOwner) { singleTask ->
      when (singleTask) {
        is TasksViewModel.SingleTaskState.DeleteTask -> {
          deleteSingleTaskAction(singleTask)
        }
      }
    }
  }

  private fun deleteSingleTaskAction(singleTask: TasksViewModel.SingleTaskState.DeleteTask) {
    Snackbar.make(
      requireView(),
      "${getString(R.string.delete_single_task)}: ${singleTask.task.name}",
      Snackbar.LENGTH_SHORT
    )
      .setAction(R.string.undo) {
        viewModel.onUndoDeleteClicked(singleTask.task)
      }
      .show()
  }

  private fun loadingListState(binding: FragTasksBinding) {
    binding.linearLayoutLoading.visibility = View.VISIBLE
    binding.linearLayoutNoTasks.visibility = View.GONE
    binding.recyclerViewTasks.visibility = View.GONE
  }

  private fun errorListState(binding: FragTasksBinding) {
    binding.linearLayoutLoading.visibility = View.GONE
    binding.linearLayoutNoTasks.visibility = View.VISIBLE
    binding.recyclerViewTasks.visibility = View.GONE
    Toast.makeText(context, R.string.generic_error, Toast.LENGTH_SHORT).show()
  }

  private fun successfulListState(
    binding: FragTasksBinding,
    taskAdapter: TaskAdapter,
    taskState: TasksViewModel.TaskListState.Success
  ) {
    binding.linearLayoutLoading.visibility = View.GONE
    binding.linearLayoutNoTasks.visibility = View.GONE
    binding.recyclerViewTasks.visibility = View.VISIBLE
    taskAdapter.submitList(taskState.list)
  }

  private fun emptyListState(
    binding: FragTasksBinding,
    taskAdapter: TaskAdapter
  ) {
    binding.linearLayoutLoading.visibility = View.GONE
    binding.linearLayoutNoTasks.visibility = View.VISIBLE
    binding.recyclerViewTasks.visibility = View.GONE
    taskAdapter.submitList(emptyList())
  }

  private fun FragTasksBinding.clickOnFabButton() {
    fabAddTask.setOnClickListener { view ->
      val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
        getString(R.string.new_task),
        null
      )
      view.findNavController().navigate(action)
    }
  }

  private fun actionAfterDeletingCompletedTasks() {
    setFragmentResultListener("delete_tasks_request") { _, bundle ->
      when (bundle.get("delete_tasks_result")) {
        true -> viewModel.deleteCompletedTasks()
        else -> {}
      }

    }
  }

  private fun actionAfterCreateEditTask() {
    setFragmentResultListener("add_edit_request") { _, bundle ->
      when (bundle.get("add_edit_result")) {
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
  }

  private fun FragTasksBinding.swipeListItem(taskAdapter: TaskAdapter) {
    ItemTouchHelper(object :
      ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder, target: ViewHolder
      ): Boolean {
        return false
      }

      override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val task = taskAdapter.currentList[viewHolder.adapterPosition]
        viewModel.onTaskSwiped(task)
      }
    }).attachToRecyclerView(recyclerViewTasks)
  }

  private fun deleteCompletedAction(): Boolean {
    val action = TasksFragmentDirections.actionGlobalDeleteCompletedTasksFragment()
    view?.findNavController()?.navigate(action)
    return true
  }

  private fun hideCompletedAction(item: MenuItem): Boolean {
    item.isChecked = !item.isChecked
    viewModel.hideCompletedTasks(item.isChecked)
    return true
  }

  private fun sortByDateAction(): Boolean {
    viewModel.sortTasksByDate()
    return true
  }

  private fun sortByNameAction(): Boolean {
    viewModel.sortTasksByName()
    return true
  }
}
