package com.example.pomodoroapp.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme
import com.example.pomodoroapp.ui.components.BottomBar
import com.example.pomodoroapp.ui.components.TopBar
import com.example.pomodoroapp.ui.history.HistoryScreen
import com.example.pomodoroapp.ui.home.HomeScreen
import com.example.pomodoroapp.ui.tasks.TaskListScreen

const val TIMER = "Timer"
const val TASKS = "Tasks"
const val HISTORY = "History"

@Composable
fun MainScreen(modifier: Modifier) {
    val navController = rememberNavController()
    var currentTitle by remember { mutableStateOf(TIMER) }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            currentTitle = when (it.destination.route) {
                TIMER -> TIMER
                TASKS -> "Task List Screen"
                HISTORY -> HISTORY
                else -> it.destination.route ?: ""
            }
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = { TopBar(title = currentTitle) },
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = TIMER,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp) //疊加上內容的padding
        ) {
            composable(TIMER) {
                HomeScreen(
                    onNavigateToTaskList = {
                        navController.navigate(TASKS){
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(TASKS) {
                TaskListScreen()
            }
            composable(HISTORY) {
                HistoryScreen()
            }
        }
    }
}

//淺色模式
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainScreenPreview() {
    PomodoroAppTheme {
        MainScreen(modifier = Modifier.fillMaxSize())
    }
}

//深色模式
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenDarkPreview() {
    PomodoroAppTheme {
        MainScreen(modifier = Modifier.fillMaxSize())
    }
}