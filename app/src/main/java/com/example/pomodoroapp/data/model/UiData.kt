package com.example.pomodoroapp.data.model

import com.example.pomodoroapp.data.sources.database.HistoryWithTask
import com.example.pomodoroapp.data.sources.database.TaskEntity
import com.example.pomodoroapp.data.sources.database.TaskStatus

class TaskUIData(val id: Int, val description: String, val isChoose: Boolean) {
    companion object {
        fun build(entity: TaskEntity): TaskUIData =
            TaskUIData(entity.id, entity.description, entity.status == TaskStatus.IN_PROGRESS)
    }
}

data class HistoryUIData(
    val taskId: Int,
    val title: String,
    val timeCount: Int
){
    companion object {
        fun build(historyWithTask: HistoryWithTask): HistoryUIData =
            HistoryUIData(
                taskId = historyWithTask.taskId,
                title = historyWithTask.taskDescription,
                timeCount = 1 // 每條歷史記錄作為一次計數
            )
    }
}
