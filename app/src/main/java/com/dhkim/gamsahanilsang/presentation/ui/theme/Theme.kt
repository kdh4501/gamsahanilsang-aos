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
    primary = Color(0xFF1D1D1D), // 어두운 배경색
    onPrimary = Color(0xFFE0E0E0), // 밝은 텍스트 색상
    background = Color(0xFF121212), // 기본 배경색
    onBackground = Color(0xFFFFFFFF), // 배경 위의 텍스트 색상
    surface = Color(0xFF2C2C2C), // 카드 및 다이얼로그 배경
    onSurface = Color(0xFFE0E0E0) // 표면 위의 텍스트 색상
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF7F7F7), // 밝은 배경색
    onPrimary = Color(0xFF1D1D1D), // 어두운 텍스트 색상
    background = Color(0xFFFFFFFF), // 기본 배경색
    onBackground = Color(0xFF1D1D1D), // 배경 위의 텍스트 색상
    surface = Color(0xFFF0F0F0), // 카드 및 다이얼로그 배경
    onSurface = Color(0xFF1D1D1D) // 표면 위의 텍스트 색상
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
