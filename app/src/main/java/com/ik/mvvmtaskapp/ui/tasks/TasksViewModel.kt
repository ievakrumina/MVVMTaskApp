package com.ik.mvvmtaskapp.ui.tasks

import androidx.lifecycle.*
import com.ik.mvvmtaskapp.OpenForTesting
import com.ik.mvvmtaskapp.data.Resource
import com.ik.mvvmtaskapp.data.Task
import com.ik.mvvmtaskapp.data.TaskRepository
import com.ik.mvvmtaskapp.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TaskViewModel"

@OpenForTesting
@HiltViewModel
class TasksViewModel @Inject constructor(
  private val taskRepository: TaskRepository
) : ViewModel() {

  sealed class TaskListState {
    object Error : TaskListState()
    object Loading : TaskListState()
    object Empty : TaskListState()
    class Success(val list: List<Task>) : TaskListState()
  }

  sealed class SingleTaskState {
    class DeleteTask(val task: Task) : SingleTaskState()
  }

  private val _tasks = MutableLiveData<TaskListState>()
  val tasks: LiveData<TaskListState>
    get() = _tasks

  private val _singleTask = SingleLiveEvent<SingleTaskState>()
  val singleTask: SingleLiveEvent<SingleTaskState>
    get() = _singleTask

  private var searchQuery = ""
  private var sortOrder = SortOrder.BY_NAME
  private var hideCompleted = false

  fun searchQueryTasks(query: String) {
    searchQuery = query
    getTasks()
  }

  fun getSearchQuery(): String = searchQuery

  fun hideCompletedTasks(checked: Boolean) {
    hideCompleted = checked
    getTasks()
  }

  fun getHideCompletedStatus(): Boolean = hideCompleted

  fun sortTasksByDate() {
    sortOrder = SortOrder.BY_DATE
    getTasks()
  }

  fun sortTasksByName() {
    sortOrder = SortOrder.BY_NAME
    getTasks()
  }

  fun getTasks() {
    _tasks.postValue(TaskListState.Loading)
    //Launch IO thread
    viewModelScope.launch {
      //Observe list of tasks from repository
      taskRepository.getTasks(searchQuery, sortOrder, hideCompleted)
        .collect { taskList ->
          handleTaskList(taskList)
        }
    }
  }

  fun onTaskCheckChanged(task: Task, isChecked: Boolean) =
    viewModelScope.launch {
      taskRepository.updateTask(task.copy(checked = isChecked))
    }

  fun onTaskSwiped(task: Task) = viewModelScope.launch {
    _singleTask.postValue(SingleTaskState.DeleteTask(task))
    taskRepository.deleteTask(task)
  }

  fun onUndoDeleteClicked(task: Task) = viewModelScope.launch {
    taskRepository.insertTask(task)
  }

  private fun handleTaskList(taskList: Resource<List<Task>>) {
    when (taskList) {
      is Resource.Error -> _tasks.postValue(TaskListState.Error)
      is Resource.Loading -> _tasks.postValue(TaskListState.Loading)
      is Resource.Success -> {
        handleSuccessfulTaskList(taskList)
      }
    }
  }

  private fun handleSuccessfulTaskList(taskList: Resource.Success<List<Task>>) {
    when (taskList.data.isEmpty()) {
      true -> _tasks.postValue(TaskListState.Empty)
      //Post value to live data once the list is received
      false -> _tasks.postValue(TaskListState.Success(taskList.data))
    }
  }

}

enum class SortOrder { BY_NAME, BY_DATE }