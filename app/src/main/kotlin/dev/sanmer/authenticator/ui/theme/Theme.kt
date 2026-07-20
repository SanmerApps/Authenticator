package dev.sanmer.authenticator.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = MaterialTheme(
    colorScheme = colorScheme(darkMode),
    shapes = Shapes,
    typography = Typography,
    content = content
)

@Composable
private fun colorScheme(
    darkMode: Boolean,
    context: Context = LocalContext.current
) = when {
    darkMode -> dynamicDarkColorScheme(context)
    else -> dynamicLightColorScheme(context)
}
