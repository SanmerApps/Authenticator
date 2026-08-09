package dev.sanmer.auth.ntp

import kotlin.time.Clock
import kotlin.time.Duration

class NtpClock(
    private val clock: Clock,
    val message: NtpMessage,
    val offset: Duration,
    val rtt: Duration
) : Clock by clock