package com.example.pomodoroapp.ui.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomodoroapp.R
import com.example.pomodoroapp.data.model.TaskUIData
import com.example.pomodoroapp.ui.theme.onTextHint


@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    taskListViewModel: TaskListViewModel = hiltViewModel()
) {
    val tasks by taskListViewModel.tasks.collectAsStateWithLifecycle()
    var newTaskText by remember { mutableStateOf("") }
    TaskListComponent(
        modifier = modifier,
        newTaskText = newTaskText,
        tasks = tasks,
        onInputValueChange = { newTaskText = it },
        onClickEvent = {
            taskListViewModel.onClickEvent(it)
            if (it is TaskScreenClickEvent.AddNewTask) {
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
    tasks: List<TaskUIData>,
    onClickEvent: (TaskScreenClickEvent) -> Unit = {},
    onInputValueChange: (String) -> Unit = {},
) {
    //編輯敘述的彈窗
    var showEditDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskUIData?>(null) }
    var editedTaskText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = newTaskText,
            onValueChange = { onInputValueChange(it) },
            label = { Text(stringResource(R.string.hint_txt_new_task)) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium //這會有圓角效果
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Add
        Button(
            modifier = Modifier.align(Alignment.End), // 對齊右邊
            shape = MaterialTheme.shapes.medium,
            onClick = {
                if (newTaskText.isNotBlank()) {
                    onClickEvent(TaskScreenClickEvent.AddNewTask(newTaskText))
                }
            }
        ) {
            Text(
                text = stringResource(R.string.btn_add),
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
                    text = stringResource(R.string.hint_txt_no_task),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTextHint,
                    textAlign = TextAlign.Center,
                    )
            }
        } else {
            LazyColumn {
                items(tasks) { task ->
                    SwipeBoxAtEnd(
                        taskName = task.description,
                        isSelected = task.isChoose,
                        onSelectTask = {
                            onClickEvent(TaskScreenClickEvent.SelectTask(task))
                        },
                        onDeleteTask = {
                            onClickEvent(TaskScreenClickEvent.DeleteTask(task))
                        },
                        onEditTask = {
                            taskToEdit = task
                            editedTaskText = task.description
                            showEditDialog = true
                        }
                    )
                }
            }
        }
    }

    // 編輯任務敘述的Dialog
    if (showEditDialog && taskToEdit != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.edit_task_title)) },
            text = {
                //內容用輸入框
                OutlinedTextField(
                    value = editedTaskText,
                    onValueChange = { editedTaskText = it },
                    label = { Text(stringResource(R.string.hint_txt_edit_task)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            },
            //確定按鈕
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedTaskText.isNotBlank() && taskToEdit != null) {
                            onClickEvent(
                                TaskScreenClickEvent.EditTask(
                                    taskToEdit!!.copy(description = editedTaskText)
                                )
                            )
                            showEditDialog = false
                            taskToEdit = null
                        }
                    }
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}

@Preview
@Composable
fun TaskListScreenPreview() {
    Surface {
        TaskListComponent(
            modifier = Modifier.padding(16.dp),
            newTaskText = stringResource(R.string.hint_txt_new_task),
            tasks = listOf(
                TaskUIData(id = 1, description = "Task 1", isChoose = false),
                TaskUIData(id = 2, description = "Task 2", isChoose = true)
            )
        )
    }
}

@Preview
@Composable
fun TaskListEmptyScreenPreview() {
    Surface {
        TaskListComponent(
            modifier = Modifier.padding(16.dp),
            newTaskText = stringResource(R.string.hint_txt_new_task),
            tasks = listOf()
        )
    }
}