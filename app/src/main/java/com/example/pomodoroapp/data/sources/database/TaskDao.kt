package com.example.pomodoroapp.data.sources.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Update
    suspend fun updateTasks(tasks: List<TaskEntity>)

    @Query("UPDATE tasks SET description = :newDescription WHERE id = :taskId")
    suspend fun updateTaskDescription(taskId: Int, newDescription: String)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isActive = :isActive WHERE id = :taskId")
    suspend fun changeTaskActiveStatus(taskId: Int, isActive: Boolean)

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllTasksExceptNotActive(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity

    @Query("SELECT * FROM tasks WHERE status = 'IN_PROGRESS'")
    suspend fun getInProgressTask(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE status = 'IN_PROGRESS'")
    fun getInProgressTaskFlow(): Flow<TaskEntity?>
}