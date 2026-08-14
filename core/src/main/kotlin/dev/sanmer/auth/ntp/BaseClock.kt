package dev.sanmer.auth.ntp

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal class BaseClock(
    val instant: Instant = Clock.System.now(),
    val mark: TimeMark = TimeSource.Monotonic.markNow(),
) : Clock {
    override fun now() = instant + mark.elapsedNow()

    fun sync(offset: Duration) = BaseClock(
        instant = now() + offset,
        mark = TimeSource.Monotonic.markNow()
    )
}