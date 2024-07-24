package dev.sanmer.authenticator.ui.ktx

import androidx.compose.runtime.Composable

@Composable
fun <T> T.letCompose(content: @Composable (T) -> Unit) =
    @Composable { content(this) }