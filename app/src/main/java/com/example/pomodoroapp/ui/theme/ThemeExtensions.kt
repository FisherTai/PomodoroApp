package com.example.pomodoroapp.ui.theme

import android.provider.CalendarContract
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//擴展的主題顏色

//val ColorScheme.onTextHint: Color
//    @Composable get() = if (isSystemInDarkTheme()) HintColorDark else HintColorLight

val ColorScheme.onTextHint: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)


