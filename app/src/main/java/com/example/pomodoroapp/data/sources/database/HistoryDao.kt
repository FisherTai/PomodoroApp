package com.example.pomodoroapp.data.sources.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    /* -------------------------------------插入操作------------------------------------- */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<HistoryEntity>)

    /* -------------------------------------更新操作(目前不需要)------------------------------------- */
//    @Update
//    suspend fun updateHistory(history: HistoryEntity)

    /* -------------------------------------刪除操作------------------------------------- */
    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM histories WHERE id = :historyId")
    suspend fun deleteHistoryById(historyId: Int)

    @Query("DELETE FROM histories WHERE taskId = :taskId")
    suspend fun deleteHistoriesByTaskId(taskId: Int)

    @Query("DELETE FROM histories")
    suspend fun deleteAllHistories()

    /* -------------------------------------查詢操作------------------------------------- */

//    @Query("SELECT * FROM histories WHERE id = :historyId")
//    suspend fun getHistoryById(historyId: Int): HistoryEntity?

    @Query("SELECT * FROM histories")
    fun getAllHistories(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM histories WHERE taskId = :taskId")
    fun getHistoriesByTaskId(taskId: Int): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM histories WHERE timestamp >= :startOfDay AND timestamp < :startOfNextDay")
    fun getHistoriesByDate(startOfDay: Long, startOfNextDay: Long): Flow<List<HistoryEntity>>

    @Query("""
        SELECT h.id, h.taskId, h.timestamp, t.description as taskDescription
        FROM histories h
        INNER JOIN tasks t ON h.taskId = t.id
        ORDER BY h.timestamp DESC
        """
    )
    fun getAllHistoriesWithTaskDescription(): Flow<List<HistoryWithTask>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id IN (SELECT DISTINCT taskId FROM histories)")
    fun getTasksWithHistories(): Flow<List<TaskWithHistories>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithHistories(taskId: Int): Flow<TaskWithHistories?>

    /* -------------------------------------統計查詢------------------------------------- */
    @Query("SELECT COUNT(*) FROM histories WHERE taskId = :taskId")
    suspend fun getHistoryCountByTaskId(taskId: Int): Int

    @Query("SELECT COUNT(*) FROM histories WHERE timestamp >= :startOfDay AND timestamp < :startOfNextDay")
    suspend fun getHistoryCountByDate(startOfDay: Long, startOfNextDay: Long): Int

}