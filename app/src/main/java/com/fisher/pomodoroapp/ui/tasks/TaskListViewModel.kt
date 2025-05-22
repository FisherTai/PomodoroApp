package com.fisher.pomodoroapp.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisher.pomodoroapp.data.model.TaskUIData
import com.fisher.pomodoroapp.data.repository.TaskRepository
import com.fisher.pomodoroapp.data.sources.database.TaskEntity
import com.fisher.pomodoroapp.data.sources.database.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TaskScreenClickEvent {
    data class AddNewTask(val description: String) : TaskScreenClickEvent()
    data class SelectTask(val task: TaskUIData) : TaskScreenClickEvent()
    data class DeleteTask(val task: TaskUIData) : TaskScreenClickEvent()
    data class EditTask(val task: TaskUIData) : TaskScreenClickEvent()
}

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val tasks = taskRepository.tasks.map { taskEntities ->
        //轉換成Tasks
        mutableListOf<TaskUIData>().apply {
            taskEntities.forEach { taskEntity ->
                add(TaskUIData.build(taskEntity))
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
            is TaskScreenClickEvent.DeleteTask -> {
                closeTask(event.task)
            }
            is TaskScreenClickEvent.EditTask -> {
                editTask(event.task)
            }
        }
    }

    private fun closeTask(task: TaskUIData) = viewModelScope.launch {
        taskRepository.closeTask(task.id)
    }

    private fun editTask(task: TaskUIData) = viewModelScope.launch {
        taskRepository.updateTaskDescription(task.id, task.description)
    }

    private fun selectTask(selectTask: TaskUIData) {
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