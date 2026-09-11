package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AnimePink,
    secondary = AnimeCyan,
    tertiary = AnimeGold,
    background = AnimeBackground,
    surface = AnimeSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = AnimeTextPrimary,
    onSurface = AnimeTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AnimePinkDark,
    secondary = AnimePurple,
    tertiary = AnimeGold,
    background = Color(0xFFFFF0F5),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF2E1A47),
    onSurface = Color(0xFF2E1A47)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to rich anime dark vibe for stage and room lighting
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
