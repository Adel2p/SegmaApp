package com.noob.apps.mvvmcountries.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.common.util.Base64Utils
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and


object AESUtils {
    fun doDecrypt(encodekey: String, encrptedStr: String): String? {
        try {
            val dcipher = Cipher.getInstance("AES")
            val secretKeySpec: SecretKeySpec = getKey(encodekey)
            dcipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            // decode with base64 to get bytes
            val dec = Base64Utils.decode(encrptedStr)
            val utf8 = dcipher.doFinal(dec)

            // create new string based on the specified charset
            return String(utf8, StandardCharsets.UTF_8)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getKey(encodekey: String): SecretKeySpec {
        var key = encodekey.toByteArray(StandardCharsets.UTF_8)
        val sha: MessageDigest = MessageDigest.getInstance("SHA-256")
        key = sha.digest(key)
        println(byteArrayToHexString(key))
        key = key.copyOf(32) // use only first 256 bit
        println(byteArrayToHexString(key))
        return SecretKeySpec(key, "AES")
    }

    private fun byteArrayToHexString(b: ByteArray): String {
        var result = ""
        for (i in b.indices) {
            result += ((b[i] and 0xff.toByte()) + 0x100).toString(16).substring(1)
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(
        strToDecrypt: String?,
        mobileNumber: String?,
        mail: String?,
        userId: String?, date: String?
    ): String? {
        val  SIGMA="SIGMA"
        val  dollarSign="$"
        val  dash="_"
        val  percentage="%"
        val  hash="#"
        val test="{P@ssw0rd@NoHopeYet$01554587098%Medhat@gmail.com#9e72963d-fb81-474f-9247-43a1940368fb_20211219%SIGMA}"
        val SECRET_KEY =
            "{P@ssw0rd@NoHopeYet$dollarSign$mobileNumber$percentage$mail$hash$userId$dash$date$percentage$SIGMA}"
        return strToDecrypt?.let { doDecrypt(SECRET_KEY, it) }
//        try {
//            val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
//            val ivspec = IvParameterSpec(iv)
//            val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
//            val spec: KeySpec = PBEKeySpec(SECRET_KEY.toCharArray(), SALT.toByteArray(), 65536, 256)
//            val tmp: SecretKey = factory.generateSecret(spec)
//            val secretKey = SecretKeySpec(tmp.encoded, "AES")
//            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
//            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec)
//            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
//        } catch (e: Exception) {
//            println("Error while decrypting: $e")
//        }
//        return null
    }

}