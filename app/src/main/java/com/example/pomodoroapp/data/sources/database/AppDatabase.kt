package com.example.pomodoroapp.data.sources.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskEntity::class, HistoryEntity::class], version = 3)
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

        val MIGRATION_2_3 = object : Migration(2, 3) { // Corrected version from (1,2) to (2,3)
            override fun migrate(database: SupportSQLiteDatabase) {

                // 建立新版本的表
                database.execSQL("""
                    CREATE TABLE tasks_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        description TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        status TEXT NOT NULL 
                    )
                """.trimIndent())

                // 將舊的表的資料複製到新表，處理舊的isActive參數
                database.execSQL("""
                    INSERT INTO tasks_new (id, description, createdAt, status)
                    SELECT id, description, createdAt,
                        CASE
                            WHEN isActive = 0 THEN 'DELETED' 
                            ELSE status 
                        END
                    FROM tasks
                """.trimIndent())

                // 刪除原本的表
                database.execSQL("DROP TABLE tasks")

                // 更名成原本的名字
                database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }
    }
}