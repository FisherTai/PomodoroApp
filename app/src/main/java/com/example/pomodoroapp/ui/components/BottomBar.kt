package com.example.pomodoroapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.pomodoroapp.ui.HISTORY
import com.example.pomodoroapp.ui.HOME
import com.example.pomodoroapp.ui.TASKS

data class BottomNavItem(
    val name: String,
    val route: String,
//    val icon: Int
    val icon: ImageVector
)

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(TASKS, TASKS, Icons.AutoMirrored.Filled.List),
        BottomNavItem(HOME, HOME, Icons.Default.Home),
        BottomNavItem(HISTORY, HISTORY, Icons.Default.Menu),
    )

    var selectedItemRoute by remember { mutableStateOf(HOME) } // 初始選中項

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            selectedItemRoute = backStackEntry.destination.route ?: HOME
        }
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = item.route == selectedItemRoute,
                onClick = {
                    // 防止重複點擊同一個項目造成堆疊
                    if(item.route != selectedItemRoute){
                        navController.navigate(item.route) {
                            /*
                            1. popUpTo(navController.graph.startDestinationId)
                                表示當導覽到新項目時，會把 back stack 清到起始頁，避免堆太多頁面
                                saveState = true 會保留前一頁的狀態（例如捲動位置、輸入內容等）
                             */
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            //如果新目標頁面已經在最上面，就不會重新建立一個新的副本。
                            launchSingleTop = true
                            // 如果曾經去過這個頁面，重新點擊時會回復之前的狀態（而不是重新開一個新的空白頁面）
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}