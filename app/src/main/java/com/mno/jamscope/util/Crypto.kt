package com.mno.jamscope.util

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object Crypto {
//    private const val KEY_ALIAS = "secret"
//    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
//    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
//    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
//    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    private val key: SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
    private val iv = ByteArray(16)

    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16).apply { SecureRandom().nextBytes(this) }
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        val encrypted = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        return Base64.getEncoder().encodeToString(iv + encrypted)
    }

    fun decrypt(encryptedText: String): String {
        val decoded = Base64.getDecoder().decode(encryptedText)
        val iv = decoded.copyOfRange(0, 16)
        val encrypted = decoded.copyOfRange(16, decoded.size)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }
}

//class Crypto {
//    fun hashPassword(password: String, salt: ByteArray = generateSalt()): String {
//        val iterations = 100_000
//        val keyLength = 256
//        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
//        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
//        val hash = factory.generateSecret(spec).encoded
//
//        // Concatena salt + hash e transforma em Base64 para armazenar
//        return Base64.encodeToString(salt + hash, Base64.NO_WRAP)
//    }
//
//    fun generateSalt(): ByteArray {
//        val salt = ByteArray(16) // 16 bytes = 128 bits
//        SecureRandom().nextBytes(salt)
//        return salt
//    }
//
//    fun verifyPassword(storedHash: String, inputPassword: String): Boolean {
//        val decoded = Base64.decode(storedHash, Base64.NO_WRAP)
//
//        val salt = decoded.copyOfRange(0, 16)
//        val storedHashBytes = decoded.copyOfRange(16, decoded.size)
//
//        val newHash = hashPassword(inputPassword, salt)
//        val newHashBytes = Base64.decode(newHash, Base64.NO_WRAP).copyOfRange(16, decoded.size)
//
//        return storedHashBytes.contentEquals(newHashBytes)
//    }
//}