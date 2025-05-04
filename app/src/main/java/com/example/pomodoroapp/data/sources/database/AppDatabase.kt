package com.example.pomodoroapp.data.sources.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskEntity::class, HistoryEntity::class], version = 2)
@TypeConverters(EnumAndStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun historyDao(): HistoryDao
    
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 新增 isActive 欄位，並將預設值設為 true
                database.execSQL("ALTER TABLE tasks ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}