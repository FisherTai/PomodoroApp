package com.fisher.pomodoroapp.data.sources.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Update
    suspend fun updateTasks(tasks: List<TaskEntity>)

    @Query("UPDATE tasks SET description = :newDescription WHERE id = :taskId")
    suspend fun updateTaskDescription(taskId: Int, newDescription: String)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun changeTaskActiveStatus(taskId: Int, status: TaskStatus)

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status != :status ORDER BY createdAt DESC")
    fun getAllTasksExceptNotActive(status: TaskStatus = TaskStatus.DELETED): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity

    @Query("SELECT * FROM tasks WHERE status = :status")
    suspend fun getInProgressTask(status: TaskStatus = TaskStatus.IN_PROGRESS): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE status = :status LIMIT 1")
    fun getInProgressTaskFlow(status: TaskStatus = TaskStatus.IN_PROGRESS): Flow<TaskEntity?>
}