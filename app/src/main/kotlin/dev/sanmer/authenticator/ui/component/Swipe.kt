package dev.sanmer.authenticator.ui.component

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun SwipeContent(
    content: @Composable (() -> Unit) -> Unit,
    surface: @Composable () -> Unit
) {
    var deltaValue by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var targetOffsetX = remember { 0f }

    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(expanded) {
        if (expanded) animate(offsetX, -targetOffsetX, animationSpec = tween(200)) { value, _ ->
            offsetX = value
        }
    }

    var released by remember { mutableStateOf(false) }
    LaunchedEffect(released) {
        if (released) animate(offsetX, 0f, animationSpec = tween(200)) { value, _ ->
            offsetX = value
        }
    }

    Box {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .onSizeChanged { targetOffsetX = it.width.toFloat() }
        ) {
            content { released = true }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (targetOffsetX == 0f) return@rememberDraggableState
                        val p = 1f - abs(offsetX) / (targetOffsetX * 1.25f)
                        offsetX += delta * min(p, 1f)
                        deltaValue = delta
                    },
                    onDragStarted = {
                        expanded = false
                        released = false
                        deltaValue = 0f
                    },
                    onDragStopped = {
                        if (deltaValue > 0) {
                            released = true
                        } else {
                            if (abs(offsetX) / targetOffsetX > 0.35) {
                                expanded = true
                            } else {
                                released = true
                            }
                        }
                    }
                )
        ) {
            surface()
        }
    }
}

@Composable
fun SwipeFloor(
    onSwipe: (SwipeValue) -> Unit,
    floor: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val currentOnSwipe by rememberUpdatedState(newValue = onSwipe)

    var offsetX by remember { mutableFloatStateOf(0f) }
    var triggerOffsetX = remember { 0f }
    val swipeProgress by remember {
        derivedStateOf {
            when {
                offsetX > 0 -> min(offsetX / triggerOffsetX, 1f)
                offsetX < 0 -> max(offsetX / triggerOffsetX, -1f)
                else -> 0f
            }
        }
    }

    var released by remember { mutableStateOf(false) }
    LaunchedEffect(released) {
        if (released) animate(offsetX, 0f, animationSpec = tween(300)) { value, _ ->
            offsetX = value
            currentOnSwipe(SwipeValue.Closing(swipeProgress))
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        if (offsetX != 0f) floor()

        Box(
            modifier = Modifier
                .onSizeChanged { triggerOffsetX = it.width / 5f * 2 }
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (triggerOffsetX == 0f) return@rememberDraggableState
                        val p = 1f - abs(offsetX) / (triggerOffsetX * 1.25f)
                        offsetX += delta * min(p, 1f)
                        currentOnSwipe(SwipeValue.Swiping(swipeProgress))
                    },
                    onDragStarted = {
                        currentOnSwipe(SwipeValue.Started())
                        released = false
                    },
                    onDragStopped = {
                        currentOnSwipe(SwipeValue.Stopped(swipeProgress))
                        released = true
                    }
                )
        ) {
            content()
        }
    }
}

sealed class SwipeValue(val progress: Float) {
    class Started : SwipeValue(0f)
    class Swiping(progress: Float) : SwipeValue(progress)
    class Stopped(progress: Float) : SwipeValue(progress)
    class Closing(progress: Float) : SwipeValue(progress)
}