package com.fisher.pomodoroapp.data.sources.database

import androidx.room.Embedded
import androidx.room.Relation


data class TaskWithHistories(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val histories: List<HistoryEntity>
)