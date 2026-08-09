package dev.sanmer.auth.ntp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

interface NtpServer {
    val address: String
    val port: Int get() = 123
    val timeout: Duration get() = 1.seconds

    suspend fun sync() = withContext(Dispatchers.IO) {
        val address = InetAddress.getByName(address)
        val socket = DatagramSocket()
        socket.soTimeout = timeout.toInt(DurationUnit.MILLISECONDS)

        val clock = BaseClock()
        val packet = NtpPacket()
        packet.writeVersion()
        packet.writeMode()
        packet.writePoll(64.seconds)
        packet.writePrecision(1.microseconds)

        val p = DatagramPacket(packet.bytes, NtpPacket.PACKET_SIZE, address, port)
        val t1 = clock.now()
        packet.writeTransmitTimestamp(t1)
        socket.send(p)
        socket.receive(p)
        val t4 = clock.now()
        socket.close()

        val message = NtpMessage(packet)
        val t2 = packet.readReceiveTimestamp()
        val t3 = packet.readTransmitTimestamp()
        val offset = (((t2 - t1) + (t3 - t4)) / 2)
        val rtt = ((t4 - t1) - (t3 - t2))
        NtpClock(clock.sync(offset), message, offset, rtt)
    }

    data class Custom(
        override val address: String
    ) : NtpServer

    data object Alibaba : NtpServer {
        override val address = "ntp.aliyun.com"
    }

    data object Apple : NtpServer {
        override val address = "time.apple.com"
    }

    data object Amazon : NtpServer {
        override val address = "time.aws.com"
    }

    data object Cloudflare : NtpServer {
        override val address = "time.cloudflare.com"
    }

    data object Google : NtpServer {
        override val address = "time.google.com"
    }

    data object Meta : NtpServer {
        override val address = "time.facebook.com"
    }

    data object Microsoft : NtpServer {
        override val address = "time.windows.com"
    }

    data object Tencent : NtpServer {
        override val address = "ntp.tencent.com"
    }
}