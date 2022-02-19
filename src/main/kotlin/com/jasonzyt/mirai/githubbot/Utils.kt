package com.jasonzyt.mirai.githubbot

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmName

object Utils {

    fun doHMacSha256(secret: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        val key = SecretKeySpec(secret, "HmacSHA256");
        mac.init(key)
        return mac.doFinal(data)
    }

    fun byte2hex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            val temp = Integer.toHexString(b.toInt() and 0xff)
            if (temp.length == 1) {
                sb.append("0")
            }
            sb.append(temp)
        }
        return sb.toString()
    }

    fun base64Encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    inline fun <reified T> format(fmt: String, v: T): String {
        return format(fmt, v, T::class)
    }

    fun <T> format(fmt: String, v: T, clazz: KClass<*>, prefix: String = ""): String {
        var result = fmt
        for (member in clazz.memberProperties) {
            if (!result.contains("{${prefix}${member.name}")) {
                continue
            }
            val value = member.getter.call(v)
            if (value != null) {
                result = format(result, value, member.returnType.classifier as KClass<*>, prefix + member.name + ".")
                result = result.replace("{${prefix}${member.name}}", value.toString())
            }
        }
        return result
    }

    fun getEmoji(name: String): String {
        val emojiCode = Integer.valueOf(name, 16)
        val emojiBytes: ByteArray = int2bytes(emojiCode)
        return String(emojiBytes, Charsets.UTF_32)
    }

    private fun int2bytes(num: Int): ByteArray {
        val result = ByteArray(4)
        result[0] = (num ushr 24 and 0xff).toByte()
        result[1] = (num ushr 16 and 0xff).toByte()
        result[2] = (num ushr 8 and 0xff).toByte()
        result[3] = (num ushr 0 and 0xff).toByte()
        return result
    }

}