package com.example.pomodoroapp.data.sources.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val createdAt: Long = Date().time,
    val status: TaskStatus = TaskStatus.TODO,
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED
}