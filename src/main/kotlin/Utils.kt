package com.jasonzyt.mirai.githubbot

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Utils {

    fun HMacSha256(secret: ByteArray, data: ByteArray): ByteArray {
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

}