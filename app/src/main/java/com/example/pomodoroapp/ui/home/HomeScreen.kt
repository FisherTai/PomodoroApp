package com.example.pomodoroapp.ui.home

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pomodoroapp.R

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTaskList: () -> Unit = {} // 添加導航函數參數
) {
    val countDownState by homeViewModel.countDownState.collectAsStateWithLifecycle()
    val taskDescribe by homeViewModel.taskDescribe.collectAsStateWithLifecycle()
    val timeDisplay by homeViewModel.timeDisplay.collectAsStateWithLifecycle()
    val currentPhase by homeViewModel.currentPhase.collectAsStateWithLifecycle()

    HomeContent(
        countDownState = countDownState,
        taskDescribe = taskDescribe,
        timeDisplay = timeDisplay,
        currentPhase = currentPhase,
        onClickEvent = { homeViewModel.onClickEvent(it) },
        onNavigateToTaskList = onNavigateToTaskList
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    countDownState: CountDownState,
    taskDescribe: String,
    timeDisplay: String,
    currentPhase: TimerPhase,
    onClickEvent: (event: HomeClickEvent) -> Unit,
    onNavigateToTaskList: () -> Unit = {} // 添加導航函數參數
) {
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
                text = timeDisplay,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (taskDescribe.isNotBlank()) {
                if (currentPhase == TimerPhase.FOCUS){
                    Text(
                        text = taskDescribe,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (currentPhase == TimerPhase.FOCUS) stringResource(id = R.string.timer_phase_focus) else stringResource(id = R.string.timer_phase_break),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                // 如果沒有任務描述，顯示可點擊的提示
                Text(
                    text = stringResource(id = R.string.hint_message_add_new),
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.clickable { onNavigateToTaskList() }
                )
            }
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
                    onClickEvent(HomeClickEvent.StartPauseClicked)
                }
            ) {
                Text(
                    text = if (countDownState == CountDownState.RUNNING) stringResource(R.string.btn_pause) else stringResource(R.string.btn_start),
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
                    onClickEvent(HomeClickEvent.ResetClicked)
                }) {
                Text(
                    text = stringResource(R.string.btn_reset),
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
    Surface {
        HomeContent(
            countDownState = CountDownState.STOP,
            taskDescribe = "任務描述",
            timeDisplay = "25:00",
            currentPhase = TimerPhase.FOCUS,
            onClickEvent = {  },
            onNavigateToTaskList = { }
        )
    }
}