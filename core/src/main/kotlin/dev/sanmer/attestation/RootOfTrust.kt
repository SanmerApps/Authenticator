package dev.sanmer.attestation

import dev.sanmer.attestation.RootOfTrust.BootState.Companion.asState
import dev.sanmer.encoding.encodeBase64
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Sequence

class RootOfTrust private constructor(
    val deviceLocked: Boolean,
    val verifiedBootState: BootState,
    private val verifiedBootKey: ByteArray,
    private val verifiedBootHash: ByteArray?
) {
    val verifiedBootKeyString by lazy { verifiedBootKey.encodeBase64() }
    val verifiedBootHashString by lazy { verifiedBootHash?.encodeBase64() }

    enum class BootState(internal val value: Int) {
        Verified(0),
        SelfSigned(1),
        Unverified(2),
        Failed(3),
        Unknown(-1);

        internal companion object {
            fun Int.asState() = when (this) {
                Verified.value -> Verified
                SelfSigned.value -> SelfSigned
                Unverified.value -> Unverified
                Failed.value -> Failed
                else -> Unknown
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    internal companion object {
        const val VERIFIED_BOOT_KEY_INDEX = 0
        const val DEVICE_LOCKED_INDEX = 1
        const val VERIFIED_BOOT_STATE_INDEX = 2
        const val VERIFIED_BOOT_HASH_INDEX = 3

        val EMPTY get() = RootOfTrust(
            deviceLocked = false,
            verifiedBootState = BootState.Unknown,
            verifiedBootKey = byteArrayOf(),
            verifiedBootHash = null
        )

        fun parse(values: ASN1Sequence): RootOfTrust {
            return RootOfTrust(
                deviceLocked = values.getObjectAt(DEVICE_LOCKED_INDEX).asBoolean(),
                verifiedBootState = values.getObjectAt(VERIFIED_BOOT_STATE_INDEX).asInt().asState(),
                verifiedBootKey = values.getObjectAt(VERIFIED_BOOT_KEY_INDEX).asByteArray(),
                verifiedBootHash = values.getObjectAtOrNull(VERIFIED_BOOT_HASH_INDEX)?.asByteArray()
            )
        }

        inline fun ASN1Sequence.toRootOfTrust() =
            parse(this)

        inline fun ASN1Encodable.toRootOfTrust() =
            parse(asSequence())
    }
}
