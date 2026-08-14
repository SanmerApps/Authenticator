package dev.sanmer.authenticator

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.char
import kotlin.time.Instant

object Const {
    const val GITHUB_URL = "https://github.com/SanmerApps/Authenticator"

    val INSTANT_ZERO = Instant.fromEpochSeconds(0, 0)
    inline val Instant.isZero get() = this == INSTANT_ZERO

    val TIME_DISPLAY = LocalTime.Format {
        hour(); char(':'); minute(); char(':'); second()
    }

    val DATETIME_DISPLAY = LocalDateTime.Format {
        date(LocalDate.Formats.ISO)
        char(' ')
        time(TIME_DISPLAY)
    }
}