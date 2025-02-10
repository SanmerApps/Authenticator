package dev.sanmer.ntp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

interface NtpServer {
    val address: String
    val port: Int get() = NTP_PORT
    val timeout: Duration get() = 15.seconds

    suspend fun address() = address.toInetAddress()
    suspend fun sync() = sync(this)

    object Apple : NtpServer {
        override val address = "time.apple.com"
    }

    object Cloudflare : NtpServer {
        override val address = "time.cloudflare.com"
    }

    object Google : NtpServer {
        override val address = "time.google.com"
    }

    object Microsoft : NtpServer {
        override val address = "time.windows.com"
    }

    data class NtpTime(
        val delay: Duration = 0.milliseconds,
        val offset: Duration = 0.milliseconds
    )

    private companion object Impl {
        const val NTP_PORT = 123
        const val NTP_MODE = 3
        const val NTP_VERSION = 3
        const val NTP_PACKET_SIZE = 48
        const val NTP_OFFSET = 2208988800L

        const val INDEX_VERSION = 0
        const val INDEX_RECEIVE_TIME = 32
        const val INDEX_TRANSMIT_TIME = 40

        suspend fun sync(server: NtpServer) = withContext(Dispatchers.IO) {
            val data = NtpData(NTP_PACKET_SIZE)
            data.writeNtpVersion(NTP_VERSION)

            DatagramSocket().use { socket ->
                socket.soTimeout = server.timeout.toInt(DurationUnit.MILLISECONDS)

                val request = DatagramPacket(data.bytes, data.size, server.address(), server.port)
                val t1 = System.currentTimeMillis()
                data.writeTransmitTimestamp(t1)
                socket.send(request)

                val response = DatagramPacket(data.bytes, data.size)
                socket.receive(response)
                val t4 = System.currentTimeMillis()

                val t2 = data.readTimestamp(INDEX_RECEIVE_TIME)
                val t3 = data.readTimestamp(INDEX_TRANSMIT_TIME)

                NtpTime(
                    delay = ((t4 - t1) - (t3 - t2)).milliseconds,
                    offset = (((t2 - t1) + (t3 - t4)) / 2.0).milliseconds
                )
            }
        }

        suspend fun String.toInetAddress(): InetAddress = withContext(Dispatchers.IO) {
            InetAddress.getByName(this@toInetAddress)
        }

        class NtpData(
            val bytes: ByteArray
        ) {
            constructor(size: Int) : this(
                bytes = ByteArray(size)
            )

            private val buffer = ByteBuffer.wrap(bytes)
                .order(ByteOrder.BIG_ENDIAN)

            val size inline get() = bytes.size

            fun writeNtpVersion(value: Int) {
                buffer.put(INDEX_VERSION, (NTP_MODE or (value shl 3)).toByte())
            }

            fun writeTransmitTimestamp(value: Long) {
                val seconds = (value / 1000L) + NTP_OFFSET
                val fraction = ((value % 1000L) * 0x100000000L / 1000L)
                buffer.putInt(INDEX_TRANSMIT_TIME, seconds.toInt())
                buffer.putInt(INDEX_TRANSMIT_TIME + 4, fraction.toInt())
            }

            fun readTimestamp(index: Int): Long {
                val seconds = buffer.getInt(index).toLong() and 0xFFFFFFFFL
                val fraction = buffer.getInt(index + 4).toLong() and 0xFFFFFFFFL
                return ((seconds - NTP_OFFSET) * 1000) + (fraction * 1000) / 0x100000000L
            }
        }
    }
}