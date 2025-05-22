package com.fisher.pomodoroapp.data.sources.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "histories",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE  // Task被刪除，History也會被刪除
        )
    ]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int, //關聯TaskEntity的id
    val timestamp: Long // 毫秒
)