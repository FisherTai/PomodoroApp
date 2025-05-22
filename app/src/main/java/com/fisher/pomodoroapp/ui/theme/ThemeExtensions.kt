package com.fisher.pomodoroapp.ui.theme

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

val ColorScheme.focusBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) FocusDarkBackground else FocusLightBackground

val ColorScheme.focusText: Color
    @Composable get() = if (isSystemInDarkTheme()) FocusDarkText else FocusLightText

val ColorScheme.breakBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) BreakDarkBackground else BreakLightBackground

val ColorScheme.breakText: Color
    @Composable get() = if (isSystemInDarkTheme()) BreakDarkText else BreakLightText
