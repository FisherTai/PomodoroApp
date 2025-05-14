package com.example.pomodoroapp.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pomodoroapp.R
import com.example.pomodoroapp.data.model.HistoryUIData
import com.example.pomodoroapp.ui.components.FullScreenHint

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = hiltViewModel()
    ) {
    val historyList by historyViewModel.historyList.collectAsStateWithLifecycle()
    HistoryContent(modifier, historyList)
}


@Composable
fun HistoryContent(
    modifier: Modifier = Modifier,
    historyItems: List<Any>,
) {
    if (historyItems.isNotEmpty()) {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(historyItems) {
                if (it is String) {
                    HistoryHeader(modifier, it)
                } else if (it is HistoryUIData) {
                    HistoryItem(title = it.title, timeCount = it.timeCount)
                }
            }
        }
    } else {
        FullScreenHint(
            message = stringResource(R.string.hint_txt_no_history)
        )
    }
}

@Composable
fun HistoryHeader(
    modifier: Modifier = Modifier,
    date: String,
) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = date,
        style = MaterialTheme.typography.titleMedium
    )
    HorizontalDivider()
}

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    title: String,
    timeCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text("$timeCount times", style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider()
}

@Preview
@Composable
fun HistoryScreenPreview() {
    val historyItems = mapOf(
        "Today" to listOf(
            HistoryUIData(taskId = 0, title = "Reading Kotlin documentation", timeCount = 3),
            HistoryUIData(taskId = 1, title = "Watch Compose tutorials", timeCount = 1)
        )
    )

    //historyItems扁平化
    val historyList: List<Any> = mutableListOf<Any>().apply {
        historyItems.keys.forEach {
            add(it)
            addAll(historyItems[it]!!)
        }
    }

    Surface {
        HistoryContent(historyItems = historyList)
    }
}



