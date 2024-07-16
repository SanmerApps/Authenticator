package dev.sanmer.attestation

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dev.sanmer.ktx.getObjectAtOrNull
import dev.sanmer.ktx.toASN1Sequence
import org.bouncycastle.asn1.ASN1Sequence
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class KeyAttestation private constructor(
    private val alias: String
) {
    private val keyStore by lazy { KeyStore.getInstance("AndroidKeyStore") }
    private val certificateFactory by lazy { CertificateFactory.getInstance("X.509") }

    private val certs: List<X509Certificate> by lazy {
        keyStore.getCertificateChain(alias)
            .map {
                certificateFactory.generateCertificate(
                    it.encoded.inputStream()
                ) as X509Certificate
            }
    }

    val hardwareEnforced: AuthorizationList by lazy {
        certs.getAuthorizationList(TEE_ENFORCED_INDEX) ?: AuthorizationList.EMPTY
    }

    inline val isTrusted: Boolean
        get() = hardwareEnforced.rootOfTrust.deviceLocked
                && hardwareEnforced.rootOfTrust.verifiedBootState == RootOfTrust.BootState.Verified

    inline val isUntrusted: Boolean get() = !isTrusted

    init {
        keyStore.load(null)
        generateKey(alias)
    }

    @Suppress("NOTHING_TO_INLINE")
    companion object {
        private const val ASN1_OID = "1.3.6.1.4.1.11129.2.1.17"

        private const val TEE_ENFORCED_INDEX = 7

        private val randomChallenge: ByteArray
            inline get() = ByteArray(16).apply {
                SecureRandom().nextBytes(this)
            }

        internal fun generateKey(alias: String): KeyPair {
            val keyGenParameterSpec =
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setAttestationChallenge(randomChallenge)
                    .build()

            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
            )
            keyPairGenerator.initialize(keyGenParameterSpec)
            return keyPairGenerator.generateKeyPair()
        }

        private inline fun X509Certificate.toASN1Sequence(): ASN1Sequence? {
            val bytes = getExtensionValue(ASN1_OID)
            return when {
                bytes.isNotEmpty() -> bytes.toASN1Sequence()
                else -> null
            }
        }

        internal inline fun List<X509Certificate>.getAuthorizationList(index: Int) =
            firstNotNullOfOrNull {
                it.toASN1Sequence()?.getObjectAtOrNull(index)?.let(::AuthorizationList)
            }

        private val attestations = hashMapOf<String, KeyAttestation>()

        fun getInstance(alias: String): KeyAttestation {
            return attestations.getOrPut(alias) {
                KeyAttestation(alias)
            }
        }
    }
}