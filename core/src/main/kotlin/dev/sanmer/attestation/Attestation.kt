package dev.sanmer.attestation

interface Attestation {
    val isTrusted: Boolean
    val isUntrusted: Boolean get() = !isTrusted

    class Empty : Attestation {
        override val isTrusted = false
    }
}