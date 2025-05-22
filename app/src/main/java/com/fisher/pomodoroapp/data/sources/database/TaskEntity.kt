package com.fisher.pomodoroapp.data.sources.database

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
    COMPLETED,
    DELETED  //軟刪除的flag，由於TaskEntity跟HistoryEntity做關聯了，要避免Task被刪除而影響History紀錄
}