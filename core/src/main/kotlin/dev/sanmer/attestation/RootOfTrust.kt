package dev.sanmer.attestation

import dev.sanmer.attestation.RootOfTrust.BootState.Companion.asState
import dev.sanmer.encoding.encodeBase64
import dev.sanmer.ktx.asBoolean
import dev.sanmer.ktx.asByteArray
import dev.sanmer.ktx.asInt
import dev.sanmer.ktx.asSequence
import dev.sanmer.ktx.getObjectAtOrNull
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Sequence

class RootOfTrust private constructor(
    val deviceLocked: Boolean,
    val verifiedBootState: BootState,
    private val verifiedBootKey: ByteArray,
    private val verifiedBootHash: ByteArray?
) {
    internal constructor(sequence: ASN1Sequence) : this(
        deviceLocked = sequence.getObjectAt(DEVICE_LOCKED_INDEX).asBoolean(),
        verifiedBootState = sequence.getObjectAt(VERIFIED_BOOT_STATE_INDEX).asInt().asState(),
        verifiedBootKey = sequence.getObjectAt(VERIFIED_BOOT_KEY_INDEX).asByteArray(),
        verifiedBootHash = sequence.getObjectAtOrNull(VERIFIED_BOOT_HASH_INDEX)?.asByteArray()
    )

    internal constructor(value: ASN1Encodable) : this(
        sequence = value.asSequence()
    )

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
    }
}
