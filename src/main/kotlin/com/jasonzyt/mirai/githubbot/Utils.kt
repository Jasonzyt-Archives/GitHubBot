package com.jasonzyt.mirai.githubbot

import com.jasonzyt.mirai.githubbot.selenium.Selenium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import okhttp3.OkHttpClient
import okhttp3.Request
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import sun.plugin2.message.Message
import xyz.cssxsh.selenium.browser
import xyz.cssxsh.selenium.isReady
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

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

    fun parseTimeStr(str: String): Date {
        return SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ").parse(str)
    }

    // [screenshot[https://github.com/{full_name}/issues/{number}:.js-quote-selection-container]]
    fun parseScreenshotCssSelectors(str: String) = Regex("\\[screenshot\\[(.+):(.+)]]").findAll(str)

    fun parseRemoteImageUrls(str: String) = Regex("\\[remoteImage\\[(.+)]]").findAll(str)

    fun parseLocalImagePaths(str: String) = Regex("\\[localImage\\[(.+)]]").findAll(str)

    fun getRemoteImage(url: String): ByteArray? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body?.bytes()
    }

    fun getLocalImage(path: String): File? {
        if (File(path).exists()) {
            return File(path)
        }
        return null
    }

    suspend fun getScreenshot(url: String, cssSelector: String): ByteArray? {
        val driver = Selenium(url)
        driver.waitForReady()
        val element = driver.findElement(By.cssSelector(cssSelector)) ?: return null
        element.click()
        if (element.size.width > driver.windowSize.width) {
            driver.windowSize = Dimension(element.size.width, driver.windowSize.height)
        } else if (element.size.height > driver.windowSize.height) {
            driver.windowSize = Dimension(driver.windowSize.width, element.size.height)
        }
        return element.getScreenshotAs(OutputType.BYTES)
    }

    suspend fun buildImageMessage(str: String, contact: Contact): MessageChain {
        val builder = MessageChainBuilder()
        var lastIndex = 0
        parseLocalImagePaths(str).forEach {
            val text = str.substring(lastIndex, it.range.first)
            getLocalImage(it.groupValues[1])?.let { file ->
                builder.append(text)
                builder.append(contact.uploadImage(file))
                lastIndex = it.range.last + 1
            }
        }
        parseRemoteImageUrls(str).forEach {
            val text = str.substring(lastIndex, it.range.first)
            getRemoteImage(it.groupValues[1])?.let { bytes ->
                File.createTempFile("com_jasonzyt_mirai_githubbot_", "").apply {
                    writeBytes(bytes)
                }.let { file ->
                    builder.append(text)
                    builder.append(contact.uploadImage(file))
                    lastIndex = it.range.last + 1
                }
            }
        }
        parseScreenshotCssSelectors(str).forEach {
            val text = str.substring(lastIndex, it.range.first)
            getScreenshot(it.groupValues[1], it.groupValues[2])?.let { bytes ->
                File.createTempFile("com_jasonzyt_mirai_githubbot_", ".png").apply {
                    writeBytes(bytes)
                }.let { file ->
                    builder.append(text)
                    builder.append(contact.uploadImage(file))
                    lastIndex = it.range.last + 1
                }
            }
        }
        builder.append(str.substring(lastIndex))
        return builder.build()
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

    suspend fun getIssueScreenshot(url: String, nodeId: String): ByteArray {
        PluginMain.logger.info("getIssueScreenshot: $url: $nodeId")
        Selenium.driver.get(url)
        val start = System.currentTimeMillis()
        while (true) {
            if (Selenium.driver.isReady()) break
            delay(1000L)
            val current = System.currentTimeMillis()
            if (current - start > 60_000) break
        }
        val elements = Selenium.driver.findElements(By.cssSelector(".TimelineItem"))
        for (element in elements) {
            PluginMain.logger.info("get1")
            if (element.getAttribute("data-gid") == nodeId) {
                PluginMain.logger.info("get2")
                element.click()
                return element.getScreenshotAs(OutputType.BYTES)
            }
        }
        return ByteArray(0)
    }

}