package com.example.pomodoroapp.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.model.Task
import com.example.pomodoroapp.data.repository.TaskRepository
import com.example.pomodoroapp.data.sources.database.TaskEntity
import com.example.pomodoroapp.data.sources.database.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TaskScreenClickEvent {
    data class AddNewTask(val description: String) : TaskScreenClickEvent()
    data class SelectTask(val task: Task) : TaskScreenClickEvent()
}

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val tasks = taskRepository.tasks.map { taskEntities ->
        //轉換成Tasks
        mutableListOf<Task>().apply {
            taskEntities.forEach { taskEntity ->
                add(Task.build(taskEntity))
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun onClickEvent(event: TaskScreenClickEvent) {
        when (event) {
            is TaskScreenClickEvent.AddNewTask -> {
                addNewTask(event.description)
            }
            is TaskScreenClickEvent.SelectTask -> {
                selectTask(event.task)
            }
        }
    }

    private fun selectTask(selectTask: Task) {
        viewModelScope.launch {
            val currentChooseTasks = taskRepository.getInProgressTask()
            val updatedTaskEntities = mutableListOf<TaskEntity>()
            //取消選中，再添加選中
            val unselectedTasks = currentChooseTasks.map { it.copy(status = TaskStatus.TODO) }
            updatedTaskEntities.addAll(unselectedTasks)
            taskRepository.getTask(selectTask.id)?.let {
                updatedTaskEntities.add(it.copy(status = TaskStatus.IN_PROGRESS))
            }
            taskRepository.updateTasks(updatedTaskEntities)
        }
    }

    private fun addNewTask(description: String) {
        viewModelScope.launch {
            val selectTasks = taskRepository.getInProgressTask()
            val unselectedTasks = selectTasks.map { it.copy(status = TaskStatus.TODO) }
            //先取消選中再新增Task
            taskRepository.updateTasks(unselectedTasks)
            taskRepository.addTask(description)
        }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(status = TaskStatus.COMPLETED))
        }
    }
}