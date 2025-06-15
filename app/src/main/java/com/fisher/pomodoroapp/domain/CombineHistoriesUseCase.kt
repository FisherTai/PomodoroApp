package com.fisher.pomodoroapp.domain

import com.fisher.pomodoroapp.ui.history.HistoryUIData
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

/*
 * 僅作為範例
 * 非必要的層級，依據的是 Android Modern Architecture(而非Clean Architecture) 的 Domain Layer範例
 * 通常僅用於在ViewModel過於肥大用於拆解重複、複雜的邏輯，官方亦強調此層級是可選的。
 */

/**
 * 將相同taskId的HistoryUIData合併，也就是timeCount相加
 */
class CombineHistoriesUseCase @Inject constructor() {

    operator fun invoke(list: List<HistoryUIData>): List<HistoryUIData> {
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

