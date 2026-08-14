package dev.sanmer.auth.ntp

import java.net.Inet4Address
import java.net.InetAddress
import kotlin.time.Duration
import kotlin.time.Instant

data class NtpMessage(
    val leapIndicator: LeapIndicator,
    val version: Int,
    val stratum: Stratum,
    val poll: Duration,
    val precision: Duration,
    val rootDelay: Duration,
    val rootDispersion: Duration,
    val referenceIdentifier: ReferenceIdentifier,
    val referenceTimestamp: Instant
) {
    internal constructor(packet: NtpPacket) : this(
        leapIndicator = packet.readLeapIndicator().also {
            check(it != LeapIndicator.Unknown) { "Not synchronized" }
        },
        version = packet.readVersion().also {
            check(it == 3 || it == 4) { "Not supported" }
        },
        stratum = packet.readStratum().also {
            check(it != Stratum.Unknown) { "Not synchronized" }
        },
        poll = packet.readPoll(),
        precision = packet.readPrecision(),
        rootDelay = packet.readRootDelay(),
        rootDispersion = packet.readRootDispersion(),
        referenceIdentifier = packet.readReferenceIdentifier(),
        referenceTimestamp = packet.readReferenceTimestamp()
    )

    enum class LeapIndicator {
        None,
        PlusOneSecond,
        MinusOneSecond,
        Unknown
    }

    sealed interface Stratum {
        data object Unspecified : Stratum
        data object Primary : Stratum

        @JvmInline
        value class Secondary(val stratum: Int) : Stratum {
            override fun toString() = stratum.toString()
        }

        data object Unknown : Stratum
    }

    class ReferenceIdentifier internal constructor(
        private val bytes: ByteArray
    ) {
        fun kissCode() = bytes.toString(Charsets.US_ASCII)

        fun ipv4Address(): InetAddress = Inet4Address.getByAddress(bytes)

        fun ipv6Hash() = bytes.toHexString()
    }
}
