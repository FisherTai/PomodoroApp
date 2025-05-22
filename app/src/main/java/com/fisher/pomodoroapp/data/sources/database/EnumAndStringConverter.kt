package com.fisher.pomodoroapp.data.sources.database

import androidx.room.TypeConverter


class EnumAndStringConverter {

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return TaskStatus.valueOf(value)
    }
}