package com.example.pomodoroapp.ui.tasks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomodoroapp.data.model.Task


@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    taskListViewModel: TaskListViewModel = hiltViewModel()
) {
    val tasks by taskListViewModel.tasks.collectAsStateWithLifecycle()
    val selectedTask by taskListViewModel.selectedTask.collectAsStateWithLifecycle()
    var newTaskText by remember { mutableStateOf("") }
    TaskListComponent(
        modifier = modifier,
        newTaskText = newTaskText,
        tasks = tasks,
        selectedTask = selectedTask,
        onInputValueChange = { newTaskText = it },
        onSelectTask = { task ->
            taskListViewModel.selectTask(task)
        },
        onAddTask = {
            if (newTaskText.isBlank()) {

            } else {
                taskListViewModel.addNewTask(newTaskText)
                //清空
                newTaskText = ""
            }
        },
    )
}

@Composable
fun TaskListComponent(
    modifier: Modifier = Modifier,
    newTaskText: String,
    tasks: List<Task>,
    selectedTask: Task?,
    onInputValueChange: (String) -> Unit = {},
    onAddTask: () -> Unit = {},
    onSelectTask: (Task) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = newTaskText,
            onValueChange = { onInputValueChange(it) },
            label = { Text("New task") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium //這會有圓角效果
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Add
        Button(
            modifier = Modifier.align(Alignment.End), // 對齊右邊
            shape = MaterialTheme.shapes.medium,
            onClick = {
                onAddTask()
            }
        ) {
            Text(
                text = "Add",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        //分隔線
        HorizontalDivider()

        if (tasks.isEmpty()){
            //置中、Display、hint色的Text
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks available",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        taskName = task.description,
                        isSelected = task.id == selectedTask?.id,
                        onSelectTask = {
                            onSelectTask(task)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(taskName: String, isSelected: Boolean, onSelectTask: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectTask() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Checkbox(checked = isSelected, onCheckedChange = { onSelectTask() })
//        RadioButton(selected = isSelected, onClick = { onSelectTask() })
        // 自訂圓形Checkbox
        CircularCheckbox(checked = isSelected, onCheckedChange = { onSelectTask() })

        Spacer(modifier = Modifier.width(8.dp))
        Text(taskName, style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider() // 分隔線
}

// 自定義圓形 Checkbox
@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 在進入 Canvas 的 onDraw 之前獲取顏色(因為onDraw期間無法從Theme獲取顏色)
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    val checkmarkColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .clickable { onCheckedChange() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(24.dp),
            onDraw = {
                // 繪製外圓
                drawCircle(
                    color = if (checked) primaryColor else outlineColor,
                    radius = size.minDimension / 2,
                    style = if (checked) Fill else Stroke(
                        width = 2.dp.toPx()
                    )
                )

                // 如果選中，繪製勾選標記
                if (checked) {
                    val path = Path().apply {
                        val centerX = size.width / 2
                        val centerY = size.height / 2

                        // 勾選標記的起點 (左下)
                        moveTo(centerX - size.width * 0.25f, centerY)

                        // 勾選標記的中間點 (底部)
                        lineTo(centerX * 0.8f, centerY + size.height * 0.2f)

                        // 勾選標記的終點 (右上)
                        lineTo(centerX + size.width * 0.25f, centerY - size.height * 0.2f)
                    }

                    drawPath(
                        path = path,
                        color = checkmarkColor,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun TaskListScreenPreview() {
    Surface {
        TaskListComponent(
            modifier = Modifier.padding(16.dp),
            newTaskText = "New Task",
            tasks = listOf(
                Task(id = 1, description = "Task 1", isChoose = false),
                Task(id = 2, description = "Task 2", isChoose = true)
            ),
            selectedTask = null,
            onInputValueChange = { },
            onAddTask = { },
            onSelectTask = { }
        )
    }
}

@Preview
@Composable
fun TaskListEmptyScreenPreview() {
    Surface {
        TaskListComponent(
            modifier = Modifier.padding(16.dp),
            newTaskText = "New Task",
            tasks = listOf(),
            selectedTask = null,
            onInputValueChange = { },
            onAddTask = { },
            onSelectTask = { }
        )
    }
}