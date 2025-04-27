package com.example.pomodoroapp.ui.tasks

import androidx.lifecycle.ViewModel
import com.example.pomodoroapp.data.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor() : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(listOf())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _selectedTask = MutableStateFlow(tasks.value.firstOrNull())
    val selectedTask: StateFlow<Task?> = _selectedTask

    init {
        setTestTasks()
    }

    private fun setTestTasks() {
        _tasks.value = listOf(
            Task(1, "Start new side project", false),
            Task(2, "Document features", false),
            Task(3, "Watch Compose tutorials", false)
        )
    }

    fun selectTask(selectTask: Task) {
        _selectedTask.value = tasks.value.find { it.id == selectTask.id }
    }

    fun addNewTask(description: String) {
        val newTask = Task(tasks.value.size + 1, description, false)
        //TODO 之後改成存入DB
        _tasks.value = _tasks.value.toMutableList().apply {
            add(0, newTask)
        }
        selectTask(newTask)
    }
}