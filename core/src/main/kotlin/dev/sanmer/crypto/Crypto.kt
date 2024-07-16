package dev.sanmer.crypto

import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256"
private const val ALGORITHM = "AES/CBC/PKCS5Padding"
private const val KEY_ALGORITHM = "AES"
private const val ITERATION_COUNT = 20480
private const val KEY_LENGTH = 128
private const val SALT_LENGTH = 16
private const val IV_LENGTH = 16

private val randomSalt: ByteArray
    get() = ByteArray(SALT_LENGTH).apply {
        SecureRandom().nextBytes(this)
    }

private val randomIv: ByteArray
    get() = ByteArray(IV_LENGTH).apply {
        SecureRandom().nextBytes(this)
    }

private fun CharArray.generateSecret(salt: ByteArray): SecretKey {
    val factory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM)
    val spec = PBEKeySpec(this, salt, ITERATION_COUNT, KEY_LENGTH)
    val secret = factory.generateSecret(spec)
    return SecretKeySpec(secret.encoded, KEY_ALGORITHM)
}

fun String.encryptBy(password: CharArray): String {
    val salt = randomSalt
    val iv = randomIv

    val key = password.generateSecret(salt)
    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))

    val encryptedBytes = cipher.doFinal(toByteArray(Charsets.UTF_8))
    val encryptedData = salt + iv + encryptedBytes
    return encryptedData.encodeBase64()
}

fun String.decryptBy(password: CharArray): String {
    val encryptedData = decodeBase64()
    val salt = encryptedData.copyOfRange(0, SALT_LENGTH)
    val iv = encryptedData.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
    val encryptedBytes = encryptedData.copyOfRange(SALT_LENGTH + IV_LENGTH, encryptedData.size)

    val key = password.generateSecret(salt)
    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

    val decryptedBytes = cipher.doFinal(encryptedBytes)
    return decryptedBytes.toString(Charsets.UTF_8)
}

fun String.encryptBy(password: String) = encryptBy(password.toCharArray())

fun String.decryptBy(password: String) = decryptBy(password.toCharArray())