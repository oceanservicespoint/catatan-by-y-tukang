package com.indonesiaemas.note.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Note card color palette
val noteColors = listOf(
    Color(0xFFFFFFFF), // White
    Color(0xFFFFF9C4), // Yellow
    Color(0xFFE8F5E9), // Green
    Color(0xFFE3F2FD), // Blue
    Color(0xFFFCE4EC), // Pink
    Color(0xFFF3E5F5), // Purple
    Color(0xFFFFF3E0), // Orange
    Color(0xFFE0F7FA), // Cyan
)

val noteColorsDark = listOf(
    Color(0xFF2D2D2D),
    Color(0xFF5F5229),
    Color(0xFF1E3320),
    Color(0xFF1A2C3D),
    Color(0xFF3E1F28),
    Color(0xFF2E1F3E),
    Color(0xFF3E2C16),
    Color(0xFF1A3035),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    secondaryContainer = Color(0xFFE8DEF8),
    surface = Color(0xFFFFFBFE),
    background = Color(0xFFF6F5F8),
    onSurface = Color(0xFF1C1B1F),
    onBackground = Color(0xFF1C1B1F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    secondaryContainer = Color(0xFF4A4458),
    surface = Color(0xFF1C1B1F),
    background = Color(0xFF141218),
    onSurface = Color(0xFFE6E1E5),
    onBackground = Color(0xFFE6E1E5),
)



@Composable
fun NoteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // HAPUS BARIS INI: window.statusBarColor = colorScheme.background.toArgb()
            // Baris di atas dihapus karena SDK 35 menangani warna secara otomatis (Edge-to-Edge)

            // TETAP GUNAKAN INI untuk mengatur warna ikon (Jam, Baterai, dll)
            val insetsController = WindowCompat.getInsetsController(window, view)

            // isAppearanceLightStatusBars = true -> Ikon jadi Hitam (untuk background terang)
            // isAppearanceLightStatusBars = false -> Ikon jadi Putih (untuk background gelap)
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}