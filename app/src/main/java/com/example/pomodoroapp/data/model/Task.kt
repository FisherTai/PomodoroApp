package com.example.pomodoroapp.data.model

import com.example.pomodoroapp.data.sources.database.TaskEntity
import com.example.pomodoroapp.data.sources.database.TaskStatus

class Task(val id: Int, val description: String, val isChoose: Boolean) {
    companion object {
        fun build(entity: TaskEntity): Task =
            Task(entity.id, entity.description, entity.status == TaskStatus.IN_PROGRESS)
    }
}