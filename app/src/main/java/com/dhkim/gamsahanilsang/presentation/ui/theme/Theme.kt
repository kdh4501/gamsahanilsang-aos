// Theme.kt
package com.dhkim.gamsahanilsang.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    // 필요한 색상 더 추가
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    // 필요한 색상 더 추가
)

@Composable
fun MyTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme
    val typography = androidx.compose.material3.Typography()

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = Shapes(),
        content = content
    )
}
