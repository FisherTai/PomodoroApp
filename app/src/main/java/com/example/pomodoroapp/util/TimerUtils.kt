package com.example.pomodoroapp.util

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

object TimerUtils {

    /**
     *
     * **channelFlow { … }**
     *
     * 是一個返回 Flow<T> 的構建器。和普通的 flow { } 不同，它給你一個 ProducerScope，可以使用 trySend(...) 或 send(...) 向下遊發射元素，且更靈活地配合多個協程或 Channel。
     * 它會在收集者取消時自動取消內部協程，保證資源不會泄漏。
     *
     * **ticker(...)**
     *
     * 來自 kotlinx.coroutines.channels.ticker，它返回一個 ReceiveChannel<Unit>，在指定的 initialDelayMillis 之後，每隔 delayMillis 就發送一個 Unit。
     *
     * 用它來模擬定時器的“心跳”。
     *
     * **for (event in ticker)**
     *
     * 從 ticker 通道中循環 receive。每拿到一個元素，就表示又過了一個 tickInterval。
     *
     * **remaining = (remaining - tickInterval).coerceAtLeast(0)**
     *
     * 計算新的剩余時間，保證不出現負數。
     *
     * **trySend(remaining)**
     *
     * 非阻塞地把剩余時間推送到下遊。若下遊還在處理或者已經取消，不會拋異常。
     *
     * **ticker.cancel() + break**
     *
     * 當剩余時間耗盡時，主動取消 ticker 通道並跳出循環，讓 channelFlow 結束，從而結束整個倒計時流。
     */
    @OptIn(ObsoleteCoroutinesApi::class)
    fun countdownFLow(
        totalMillis: Long,
        tickInterval: Long = 1000L
    ): Flow<Long> = channelFlow {
        // 剩餘時間
        var timeLeft = totalMillis
        // ticker channel，每隔tickInterval發送一次信號，第一次delay時間為0
        val ticker = ticker(delayMillis = tickInterval, initialDelayMillis = 0L)
        // 監聽ticker
        for (event in ticker) {
            // 每次收到ticker就減少剩餘時間，最小為0
            timeLeft = (timeLeft - tickInterval).coerceAtLeast(0)
            // 把剩餘時間發送出去
            trySend(timeLeft)
            // 為0時取消ticker
            if (timeLeft <= 0) {
                ticker.cancel()
                break
            }
        }
        // channelFlow结束後會自動close
    }
}