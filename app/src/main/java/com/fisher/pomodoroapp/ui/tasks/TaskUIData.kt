package com.fisher.pomodoroapp.ui.tasks

import com.fisher.pomodoroapp.data.sources.database.TaskEntity
import com.fisher.pomodoroapp.data.sources.database.TaskStatus

data class TaskUIData(val id: Int, val description: String, val isChoose: Boolean) {
    companion object {
        fun build(entity: TaskEntity): TaskUIData =
            TaskUIData(entity.id, entity.description, entity.status == TaskStatus.IN_PROGRESS)
    }
}
