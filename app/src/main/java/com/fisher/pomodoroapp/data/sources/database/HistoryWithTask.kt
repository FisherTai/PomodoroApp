package com.fisher.pomodoroapp.data.sources.database

data class HistoryWithTask(
    val id: Int,
    val taskId: Int,
    val timestamp: Long,
    val taskDescription: String
)