package com.fisher.pomodoroapp.ui

import android.content.res.Configuration
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fisher.pomodoroapp.R
import com.fisher.pomodoroapp.ui.theme.PomodoroAppTheme
import com.fisher.pomodoroapp.ui.components.BottomBar
import com.fisher.pomodoroapp.ui.components.TopBar
import com.fisher.pomodoroapp.ui.history.HistoryScreen
import com.fisher.pomodoroapp.ui.home.HomeScreen
import com.fisher.pomodoroapp.ui.tasks.TaskListScreen
import com.fisher.pomodoroapp.ui.theme.breakBackground
import com.fisher.pomodoroapp.ui.theme.focusBackground

const val TIMER = "Timer"
const val TASKS = "Tasks"
const val HISTORY = "History"

@Composable
fun MainScreen(modifier: Modifier) {
    val breakTitle = stringResource(R.string.title_bar_break)
    val focusTitle = stringResource(R.string.title_bar_focus)
    val taskTitle = stringResource(R.string.title_bar_tasks)
    val historyTitle = stringResource(R.string.title_bar_history)
    val focusBackground = MaterialTheme.colorScheme.focusBackground
    val breakBackground =  MaterialTheme.colorScheme.breakBackground
    val defaultBackground = MaterialTheme.colorScheme.background

    val navController = rememberNavController()
    var currentTitle by remember { mutableStateOf(focusTitle) }
    var mainBackgroundColor by remember { mutableStateOf(defaultBackground) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            currentTitle = when (it.destination.route) {
                TIMER -> focusTitle
                TASKS -> taskTitle
                HISTORY -> historyTitle
                else -> it.destination.route ?: ""
            }
        }
    }
    Scaffold(
        modifier = modifier,
        containerColor = mainBackgroundColor,
        topBar = {
            TopBar(
                mainColor = mainBackgroundColor,
                title = currentTitle
            )
        },
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
                        navController.navigate(TASKS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onFocusStateChanged = { isBreak ->
                        currentTitle = if (isBreak) breakTitle else focusTitle
                        mainBackgroundColor = if (isBreak) breakBackground else focusBackground
                    }
                )
            }
            composable(TASKS) {
                mainBackgroundColor = defaultBackground
                TaskListScreen()
            }
            composable(HISTORY) {
                mainBackgroundColor = defaultBackground
                HistoryScreen()
            }
        }
    }
}

//淺色模式
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainScreenPreview() {
    PomodoroAppTheme {
        MainScreen(modifier = Modifier.fillMaxSize())
    }
}

//深色模式
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenDarkPreview() {
    PomodoroAppTheme {
        MainScreen(modifier = Modifier.fillMaxSize())
    }
}