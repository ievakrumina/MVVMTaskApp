package com.ik.mvvmtaskapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ik.mvvmtaskapp.R
import com.ik.mvvmtaskapp.databinding.FragTasksBinding
import com.ik.mvvmtaskapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.frag_tasks) {
  private val viewModel: TasksViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val binding = FragTasksBinding.bind(view)
    val taskAdapter = TaskAdapter()

    binding.apply {
      recyclerViewTasks.apply {
        adapter = taskAdapter
        layoutManager = LinearLayoutManager(requireContext())
        setHasFixedSize(true)
      }
    }
    viewModel.tasks.observe(viewLifecycleOwner) {
      taskAdapter.submitList(it)
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
        //TODO
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}
