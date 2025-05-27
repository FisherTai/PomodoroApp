package com.fisher.pomodoroapp.ui.history

import com.fisher.pomodoroapp.data.sources.database.HistoryWithTask


data class HistoryUIData(
    val taskId: Int,
    val title: String,
    val timeCount: Int
){
    companion object {
        fun build(historyWithTask: HistoryWithTask, count: Int = 1): HistoryUIData =
            HistoryUIData(
                taskId = historyWithTask.taskId,
                title = historyWithTask.taskDescription,
                timeCount = count // 每條歷史記錄作為一次計數
            )
    }
}