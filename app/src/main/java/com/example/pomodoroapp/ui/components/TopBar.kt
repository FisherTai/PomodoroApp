package com.example.pomodoroapp.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                style = MaterialTheme.typography.displaySmall, //display是比較大的字體
            )
        }
    )
}


@Preview
@Composable
fun TopBarPreview() {
    TopBar("Pomodoro")
}