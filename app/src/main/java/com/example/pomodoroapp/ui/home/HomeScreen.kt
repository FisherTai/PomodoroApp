package com.example.pomodoroapp.ui.home

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoroapp.util.PauseAbleCountDownTimer
import java.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toKotlinDuration

enum class CountDownState {
    RUNNING,
    PAUSE,
    STOP
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // 用Duration替代timestamp表示
    val defaultTimeDuration = 25.minutes
    var countDownState by remember { mutableStateOf(CountDownState.STOP) }
    val taskDescribe by remember { mutableStateOf("測試任務") }
    //format Duration時間為mm:ss
    var timeString by remember {
        mutableStateOf(
            DateUtils.formatElapsedTime(defaultTimeDuration.inWholeSeconds)
        )
    }
    //倒數計時器
    val countDownTimer = remember(defaultTimeDuration) {
        PauseAbleCountDownTimer(
            millisInFuture = defaultTimeDuration.inWholeMilliseconds,
            countDownInterval = 1000,
            onTick = { millis ->
                val timeLeft = Duration.ofMillis(millis).toKotlinDuration().inWholeSeconds
                timeString = DateUtils.formatElapsedTime(timeLeft)
            },
            onFinish = {
//                isRunning = false
            }
        )
    }

    //運行狀態
    LaunchedEffect(countDownState) {
        when (countDownState) {
            CountDownState.RUNNING -> countDownTimer.start()
            CountDownState.PAUSE -> countDownTimer.pause()
            CountDownState.STOP -> countDownTimer.cancel()
        }
    }
    // UI實作
    Column(modifier.fillMaxSize()) {
        //元件1：Column擺放格式化後的Timer和任務描述
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = taskDescribe,
                style = MaterialTheme.typography.titleLarge
            )
        }
        //元件2：Row擺放"Start/Pause"和"Reset"按鈕
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            //開始/暫停
            Button(
                shape = MaterialTheme.shapes.medium,
                onClick = {
                countDownState = when (countDownState) {
                    CountDownState.RUNNING -> CountDownState.PAUSE
                    else -> CountDownState.RUNNING
                }
            }) {
                Text(
                    text = if (countDownState == CountDownState.RUNNING) "Pause" else "Start",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            //重設
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                onClick = {
                    timeString = DateUtils.formatElapsedTime(defaultTimeDuration.inWholeSeconds)
                    countDownState = CountDownState.STOP
                }) {
                Text(
                    text = "Reset",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}