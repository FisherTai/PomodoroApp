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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, homeViewModel: HomeViewModel = hiltViewModel()) {
    val countDownState by homeViewModel.countDownState.collectAsStateWithLifecycle()
    val taskDescribe by homeViewModel.taskDescribe.collectAsStateWithLifecycle()
    val timeDisplay by homeViewModel.timeDisplay.collectAsStateWithLifecycle()

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
                    homeViewModel.onClickEvent(HomeClickEvent.StartPauseClicked)
                }
            ) {
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
                    homeViewModel.onClickEvent(HomeClickEvent.ResetClicked)
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