package com.fisher.pomodoroapp.data.repository

import com.fisher.pomodoroapp.data.sources.database.HistoryDao
import com.fisher.pomodoroapp.data.sources.database.HistoryEntity
import com.fisher.pomodoroapp.data.sources.database.HistoryWithTask
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface HistoryRepository {
    val histories: Flow<List<HistoryEntity>>
    val historiesWithDeprecated: Flow<List<HistoryWithTask>>
    suspend fun insertHistory(history: HistoryEntity): Long
    suspend fun deleteHistory(history: HistoryEntity)
}

@Singleton
class HistoryRepositoryImpl @Inject constructor(private val historyDao: HistoryDao) :
    HistoryRepository {
    override val histories: Flow<List<HistoryEntity>> = historyDao.getAllHistories()

    override val historiesWithDeprecated: Flow<List<HistoryWithTask>>
        = historyDao.getAllHistoriesWithTaskDescription()

    override suspend fun insertHistory(history: HistoryEntity): Long =
        historyDao.insertHistory(history)

    override suspend fun deleteHistory(history: HistoryEntity) = historyDao.deleteHistory(history)
}