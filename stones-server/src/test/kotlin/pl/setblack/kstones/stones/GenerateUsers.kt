package pl.setblack.kstones.stones

import dev.neeffect.nee.security.PBKDF2Hasher
import java.nio.charset.Charset
import java.util.*

fun main() {
    val hasher = PBKDF2Hasher()
    val salt = ByteArray(16) { 0xCA.toByte() }
    val encodedPass =hasher.hashPassword("editor".toCharArray(), salt)
    println(UUID.randomUUID())
    println(Base64.getEncoder().encode(salt).toString(Charset.defaultCharset()))
    println(Base64.getEncoder().encode(encodedPass).toString(Charset.defaultCharset()))
}
