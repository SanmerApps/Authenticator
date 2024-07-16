package dev.sanmer.authenticator.ktx

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

inline fun <T> MutableStateFlow<T>.updateDistinct(function: () -> T) {
    val prevValue = value
    val nextValue = function()
    if (prevValue != nextValue) {
        update { nextValue }
    }
}