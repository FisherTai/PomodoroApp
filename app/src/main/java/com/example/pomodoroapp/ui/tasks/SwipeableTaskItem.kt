package com.example.pomodoroapp.ui.tasks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kevinnzou.compose.swipebox.SwipeBox
import com.kevinnzou.compose.swipebox.SwipeDirection
import com.kevinnzou.compose.swipebox.widget.SwipeIcon
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeBoxAtEnd(
    taskName: String,
    isSelected: Boolean,
    onSelectTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onEditTask: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    SwipeBox(
        modifier = Modifier.fillMaxWidth(),
        swipeDirection = SwipeDirection.EndToStart,
        endContentWidth = 60.dp,
        endContent = { swipeableState, endSwipeProgress ->
            //刪除按鈕
            SwipeIcon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                background = Color.Red,
                weight = 1f,
                iconSize = 20.dp,
                onClick = {
                    onDeleteTask()
                    coroutineScope.launch {
                        swipeableState.animateTo(0)
                    }
                }
            )
            //編輯按鈕
            SwipeIcon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                background =  Color.Green,
                weight = 1f,
                iconSize = 20.dp,
                onClick = {
                    onEditTask()
                    coroutineScope.launch {
                        swipeableState.animateTo(0)
                    }
                }
            )
        }
    ) { swipeableState, startSwipeProgress, endSwipeProgress ->
        TaskItem(
            taskName = taskName,
            isSelected = isSelected,
            onSelectTask = onSelectTask
        )
    }
}