package dev.sanmer.authenticator.datastore.model

import dev.sanmer.auth.ntp.NtpServer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class Preference(
    @ProtoNumber(1)
    val keyEncryptedByPassword: String = "",
    @ProtoNumber(2)
    val keyEncryptedByBiometric: String = "",
    @ProtoNumber(3)
    val ntpAddress: String = "",
    @ProtoNumber(4)
    val ntp: Ntp = Ntp.Cloudflare,
    @ProtoNumber(5)
    val secureWindow: Boolean = false
) {
    val isEncrypted inline get() = keyEncryptedByPassword.isNotEmpty()
    val isBiometric inline get() = keyEncryptedByBiometric.isNotEmpty()

    fun ntpServer() = when (ntp) {
        Ntp.Custom -> NtpServer.Custom(ntpAddress)
        Ntp.Alibaba -> NtpServer.Alibaba
        Ntp.Apple -> NtpServer.Apple
        Ntp.Amazon -> NtpServer.Amazon
        Ntp.Cloudflare -> NtpServer.Cloudflare
        Ntp.Google -> NtpServer.Google
        Ntp.Meta -> NtpServer.Meta
        Ntp.Microsoft -> NtpServer.Microsoft
        Ntp.Tencent -> NtpServer.Tencent
    }
}