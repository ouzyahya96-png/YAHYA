package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = GoldClassic,
    secondary = GoldDeep,
    background = WhitePure,
    surface = WhitePure,
    onPrimary = WhitePure,
    onSecondary = WhitePure,
    onBackground = Anthracite,
    onSurface = Anthracite,
    surfaceVariant = LightGrayBg,
    onSurfaceVariant = Anthracite
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LightColorScheme,
    typography = Typography,
    content = content
  )
}
