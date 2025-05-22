package com.fisher.pomodoroapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fisher.pomodoroapp.ui.HISTORY
import com.fisher.pomodoroapp.ui.TASKS
import com.fisher.pomodoroapp.ui.TIMER

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
        BottomNavItem(TIMER, TIMER, Icons.Default.AccessTime),
        BottomNavItem(HISTORY, HISTORY, Icons.Default.Menu),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = selected,
                onClick = {
                        navController.navigate(item.route) {
                            // 用 findStartDestination() 拿到真正的 startDestination
                            // 跳回首頁，這樣使用者每次選新項目的時候，就不會把一堆頁面堆在返回紀錄裡
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            //如果新目標頁面已經在最上面，就不會重新建立一個新的副本。
                            launchSingleTop = true
                            // 如果曾經去過這個頁面，重新點擊時會回復之前的狀態（而不是重新開一個新的空白頁面）
                            restoreState = true
                        }
                }
            )
        }
    }
}