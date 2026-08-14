package dev.sanmer.auth.ntp

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.Instant

internal class NtpPacket(
    val bytes: ByteArray
) {
    constructor(size: Int = PACKET_SIZE) : this(
        bytes = ByteArray(size)
    )

    private val buffer = ByteBuffer.wrap(bytes)
        .order(ByteOrder.BIG_ENDIAN)

    fun readLeapIndicator(): NtpMessage.LeapIndicator {
        val leap = (buffer.get(0).toInt() and 0xff) ushr 6
        return when (leap) {
            0 -> NtpMessage.LeapIndicator.None
            1 -> NtpMessage.LeapIndicator.PlusOneSecond
            2 -> NtpMessage.LeapIndicator.MinusOneSecond
            else -> NtpMessage.LeapIndicator.Unknown
        }
    }

    fun writeVersion(value: Int = VERSION) {
        val current = buffer.get(0).toInt() and 0xFF
        val updated = (current and 0xC7) or (value shl 3)
        buffer.put(0, updated.toByte())
    }

    fun readVersion(): Int {
        return ((buffer.get(0).toInt() and 0xFF) ushr 3) and 0x07
    }

    fun writeMode(value: Int = MODE_CLIENT) {
        val current = buffer.get(0).toInt() and 0xFF
        val updated = (current and 0xF8) or value
        buffer.put(0, updated.toByte())
    }

    fun readMode(): Int {
        return buffer.get(0).toInt() and 0x07
    }

    fun readStratum(): NtpMessage.Stratum {
        val stratum = buffer.get(1).toInt() and 0xFF
        return when {
            stratum == 0 -> NtpMessage.Stratum.Unspecified
            stratum == 1 -> NtpMessage.Stratum.Primary
            stratum <= 15 -> NtpMessage.Stratum.Secondary(stratum)
            else -> NtpMessage.Stratum.Unknown
        }
    }

    fun writePoll(value: Duration) {
        val value = log2(value.toDouble(DurationUnit.SECONDS))
        buffer.put(2, value.roundToInt().toByte())
    }

    fun readPoll(): Duration {
        val value = buffer.get(2).toInt()
        return Math.scalb(1.0, value).seconds
    }

    fun writePrecision(value: Duration) {
        val value = log2(value.toDouble(DurationUnit.SECONDS))
        buffer.put(3, value.roundToInt().toByte())
    }

    fun readPrecision(): Duration {
        val value = buffer.get(3).toInt()
        return Math.scalb(1.0, value).seconds
    }

    fun readRootDelay(): Duration {
        val value = buffer.getInt(4)
        return (value / FIXED_POINT_SCALE).seconds
    }

    fun readRootDispersion(): Duration {
        val value = buffer.getInt(8).toUInt().toLong()
        return (value / FIXED_POINT_SCALE).seconds
    }

    fun readReferenceIdentifier(): NtpMessage.ReferenceIdentifier {
        return NtpMessage.ReferenceIdentifier(bytes.copyOfRange(12, 16))
    }

    fun writeTimestamp(index: Int, value: Instant) {
        val seconds = value.epochSeconds + NTP_OFFSET
        val fraction = (value.nanosecondsOfSecond.toLong() shl 32) / 1_000_000_000L
        buffer.putInt(index, seconds.toInt())
        buffer.putInt(index + 4, fraction.toInt())
    }

    fun writeTransmitTimestamp(value: Instant) = writeTimestamp(40, value)

    fun readTimestamp(index: Int): Instant {
        val seconds = buffer.getInt(index).toUInt()
        val epochSeconds = seconds.toLong() - NTP_OFFSET
        val fraction = buffer.getInt(index + 4).toUInt()
        val nanosecondsOfSecond = ((fraction.toLong() * 1_000_000_000L) ushr 32).toInt()
        return Instant.fromEpochSeconds(epochSeconds, nanosecondsOfSecond)
    }

    fun readReferenceTimestamp() = readTimestamp(16)

    fun readOriginTimestamp() = readTimestamp(24)

    fun readReceiveTimestamp() = readTimestamp(32)

    fun readTransmitTimestamp() = readTimestamp(40)

    companion object Default {
        const val VERSION = 4
        const val MODE_CLIENT = 3
        const val MODE_SERVER = 4
        const val PACKET_SIZE = 48
        const val NTP_OFFSET = 2208988800L
        const val FIXED_POINT_SCALE = 65536.0
    }
}