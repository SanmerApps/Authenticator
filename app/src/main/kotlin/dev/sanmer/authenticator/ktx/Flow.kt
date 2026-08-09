package dev.sanmer.authenticator.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

fun <T> Pair<T, Flow<T>>.stateIn(
    scope: CoroutineScope,
    started: SharingStarted,
) = second.stateIn(
    scope = scope,
    started = started,
    initialValue = first
)