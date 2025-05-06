package com.example.pomodoroapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.model.HistoryUIData
import com.example.pomodoroapp.data.repository.HistoryRepository
import com.example.pomodoroapp.util.DateUtils
import com.example.pomodoroapp.util.toDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel@Inject constructor(
    historyRepository: HistoryRepository
) : ViewModel() {

    private val dateUtils = DateUtils()

    val historyList = historyRepository.historiesWithDeprecated.map { allHistoryWithTask ->
        val groupedHistory = allHistoryWithTask.groupBy { it.timestamp }
        val historyItems:MutableMap<String, List<HistoryUIData>> = mutableMapOf()
        //轉換
        groupedHistory.forEach { (key, historyWithTasks) ->
            val formattedKey = key.toDateString(dateUtils)
            historyItems[formattedKey] = historyItems.getOrDefault(formattedKey, listOf()).toMutableList().apply {
                addAll(historyWithTasks.map { HistoryUIData.build(it) })
            }
        }
        return@map historyItems
    }.map { historyItems ->
        val historyList: List<Any> = mutableListOf<Any>().apply {
            historyItems.keys.forEach {
                add(it)
                val historyUIDataList = combineHistoryUIData(historyItems[it]!!)
                addAll(historyUIDataList)
            }
        }
        return@map historyList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(),
    )

    /**
     * 將相同taskId的HistoryUIData合併，也就是timeCount相加
     */
    private fun combineHistoryUIData(list: List<HistoryUIData>): List<HistoryUIData> {
        val group = list.groupBy { it.taskId }
        return group.map { (taskId, histories) ->
            // 取第一筆資料的title,因為相同taskId的title應該要一樣
            val firstHistory = histories.first()
            HistoryUIData(
                taskId = taskId,
                title = firstHistory.title,
                timeCount = histories.sumOf { it.timeCount }
            )
        }
    }
}