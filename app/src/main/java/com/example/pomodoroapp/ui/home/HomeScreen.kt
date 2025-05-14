package com.example.pomodoroapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pomodoroapp.R
import com.example.pomodoroapp.ui.theme.breakText
import com.example.pomodoroapp.ui.theme.focusText

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTaskList: () -> Unit = {}, // 添加導航函數參數
    onFocusStateChanged: (Boolean) -> Unit = {}, //專注狀態切換
) {
    val timeDisplay by homeViewModel.timeDisplay.collectAsStateWithLifecycle()
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val currentPhase by homeViewModel.currentPhase.collectAsStateWithLifecycle()

    // 當currentPhase變化時，通知外部
    LaunchedEffect(currentPhase) {
        onFocusStateChanged(currentPhase == TimerPhase.BREAK)
    }

    HomeContent(
        modifier = modifier,
        homeUiState = homeUiState,
        timeDisplay = timeDisplay,
        onNavigateToTaskList = onNavigateToTaskList
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    homeUiState: HomeUiState,
    timeDisplay: String,
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
            if (homeUiState.hasTask()) {
                //加入圖片
                val iconResId = if (homeUiState.isFocus())
                    R.drawable.icon_focus
                else
                    R.drawable.icon_break
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "Mode Icon",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = timeDisplay,
                    style = MaterialTheme.typography.displayLarge,
                    color = if (homeUiState.isFocus()) MaterialTheme.colorScheme.focusText else MaterialTheme.colorScheme.breakText
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (homeUiState.isFocus()){
                    Text(
                        text = homeUiState.taskDescribe,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                // 如果沒有任務描述，顯示可點擊的提示
                Text(
                    text = stringResource(id = R.string.hint_message_welcome),
                    style = MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.primary),
                    textAlign = TextAlign.Center
                )
                TextButton(
                    onClick = { onNavigateToTaskList() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ){
                    Text(
                        text = buildAnnotatedString {
                            //不知道為何用withStyle來下Welcome會導致Click在色塊內被往下推，所以放棄
//                            withStyle(
//                                style = SpanStyle(
//                                    fontSize = MaterialTheme.typography.displaySmall.fontSize,
//                                )
//                            ) {
//                                append(stringResource(id = R.string.hint_message_welcome))
//                                append("\n\n")
//                            }

                            //加入背景色
                            withStyle(
                                style = SpanStyle(
                                    background = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                append(" ")
                                append(stringResource(id = R.string.hint_message_add_new1))
                                append(" ")
                            }
                            //最後的
                            append(" ")
                            append(stringResource(id = R.string.hint_message_add_new2))
                        },
                        style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                        textAlign = TextAlign.Center,
                    )
                }
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
                enabled = homeUiState.hasTask(),
                onClick = {
                    homeUiState.onClickEvent(HomeClickEvent.StartPauseClicked)
                }
            ) {
                Text(
                    text = if (homeUiState.countDownState == CountDownState.RUNNING) stringResource(R.string.btn_pause) else stringResource(R.string.btn_start),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            OutlinedButton(
                shape = MaterialTheme.shapes.medium,
                enabled = homeUiState.hasTask(),
                onClick = {
                    homeUiState.onClickEvent(HomeClickEvent.ResetClicked)
                }) {
                Text(
                    text = stringResource(R.string.btn_reset),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            //弄了個無背景色的按鈕，然後才知道有OutlinedButton...
//            Button(
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Transparent
//                ),
//                shape = MaterialTheme.shapes.medium,
//                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
//                onClick = {
//                    homeUiState.onClickEvent(HomeClickEvent.ResetClicked)
//                }) {
//                Text(
//                    text = stringResource(R.string.btn_reset),
//                    color = MaterialTheme.colorScheme.onSurface,
//                    style = MaterialTheme.typography.titleLarge
//                )
//            }
        }

    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    val homeUiState = HomeUiState(
    countDownState = CountDownState.STOP,
    taskDescribe = "任務描述",
    currentPhase = TimerPhase.FOCUS,
    onClickEvent = { }
    )
    Surface() {
        HomeContent(
            homeUiState = homeUiState,
            timeDisplay = "25:00",
            onNavigateToTaskList = { }
        )
    }
}

@Preview
@Composable
fun HomeScreenEmptyPreview() {
    val homeUiState = HomeUiState(
    countDownState = CountDownState.STOP,
    taskDescribe = "",
    currentPhase = TimerPhase.FOCUS,
    onClickEvent = { }
    )
    Surface() {
        HomeContent(
            homeUiState = homeUiState,
            timeDisplay = "25:00",
            onNavigateToTaskList = { }
        )
    }
}