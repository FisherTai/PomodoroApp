package com.example.pomodoroapp.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.model.Task
import com.example.pomodoroapp.data.repository.TaskRepository
import com.example.pomodoroapp.data.sources.database.TaskEntity
import com.example.pomodoroapp.data.sources.database.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

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

    private val _selectedTask = MutableStateFlow(tasks.value.firstOrNull())
    val selectedTask: StateFlow<Task?> = _selectedTask

    fun selectTask(selectTask: Task) {
        _selectedTask.value = tasks.value.find { it.id == selectTask.id }
    }

    fun addNewTask(description: String) {
        viewModelScope.launch {
            val id = taskRepository.addTask(description)
            // 等待 tasks Flow 更新後才做selectTask，但設置超時
            val updatedTask = withTimeoutOrNull(2000) { // 2秒超時
                tasks.first { taskList ->
                    taskList.any { it.id == id }
                }
            }
            // 如果在超時內找到了更新後的任務，則使用它
            updatedTask?.find { it.id == id }?.let {
                selectTask(it)
            }
        }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(status = TaskStatus.COMPLETED))
        }
    }
}