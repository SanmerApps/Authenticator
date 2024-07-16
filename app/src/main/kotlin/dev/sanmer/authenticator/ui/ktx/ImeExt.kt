package dev.sanmer.authenticator.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Composable
fun rememberImeVisibleState(): State<Boolean> {
    val view = LocalView.current
    val isImeVisible = remember { mutableStateOf(false) }

    DisposableEffect(view) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            isImeVisible.value = imeVisible
            insets
        }
        onDispose {}
    }

    return isImeVisible
}