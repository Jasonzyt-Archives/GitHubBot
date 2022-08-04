package com.jasonzyt.mirai.githubbot.utils

import com.jasonzyt.mirai.githubbot.PluginMain
import com.jasonzyt.mirai.githubbot.network.Network
import com.jasonzyt.mirai.githubbot.selenium.Selenium
import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import xyz.cssxsh.selenium.isReady
import java.io.File
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

object Utils {

    fun doHMacSha256(secret: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        val key = SecretKeySpec(secret, "HmacSHA256")
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

    private suspend fun takeScreenshot(url: String, cssSelector: String = ""): File? {
        Selenium.driver.get(url)
        val start = System.currentTimeMillis()
        while (true) {
            if (Selenium.driver.isReady()) break
            delay(1000L)
            val current = System.currentTimeMillis()
            if (current - start > 60_000) break
        }
        Selenium.driver.manage().window().position = Point(0, 0)
        // If no css selector specified, take the whole page screenshot
        if (cssSelector.isEmpty()) {
            Selenium.driver.manage().window().size = Selenium.driver.findElement(By.tagName("body")).size
            return Selenium.driver.getScreenshotAs(OutputType.FILE)
        }
        val elements = Selenium.driver.findElements(By.cssSelector(cssSelector))
        if (elements.isEmpty()) {
            PluginMain.logger.error("Can't find element by css selector: $cssSelector, in url: $url")
            return null
        }
        Selenium.driver.manage().window().size = elements[0].location.let {
            Dimension(it.x + elements[0].size.width, it.y + elements[0].size.height)
        }
        return elements[0].getScreenshotAs(OutputType.FILE)
    }

    suspend fun parseMessage(rawMsg: String, builder: MessageChainBuilder, contact: Contact): MessageChain {
        val regex = Regex("\\[(.+)]")
        val matches = regex.findAll(rawMsg)
        var pos = 0
        for (match in matches) {
            // Append the text before the match
            if (pos < match.range.first) {
                builder.append(rawMsg.substring(pos, match.range.first))
            }
            pos = match.range.last + 1
            // format: [{prefix}[{content}]]
            val item = match.groupValues[1]
            val prefix = item.substring(0, item.indexOf("["))
            val content = item.substring(item.indexOf("[") + 1, item.indexOf("]")).trim()

            // If the content is empty, ignore it
            if (content.isEmpty()) {
                continue
            }
            when (prefix) {
                "screenshot" -> {
                    if (!Selenium.has) {
                        PluginMain.logger.error("Dependency plugin selenium is not installed, can't take screenshot")
                        continue
                    }
                    val divPos = content.indexOf("|")
                    var cssSelector = ""
                    val url = if (divPos != -1) {
                        cssSelector = content.substring(divPos + 1)
                        content.substring(0, divPos)
                    } else {
                        content
                    }
                    val screenshot = takeScreenshot(url, cssSelector)
                    if (screenshot != null) {
                        builder.append(contact.uploadImage(screenshot))
                    }
                }
                "localImage" -> {
                    val file = File(content)
                    if (file.exists()) {
                        builder.append(contact.uploadImage(file))
                    }
                }
                "remoteImage" -> {
                    val file = Network.getImage(content)
                    if (file != null) {
                        builder.append(contact.uploadImage(file))
                    }
                }

            }
        }
        if (pos < rawMsg.length) {
            builder.append(rawMsg.substring(pos))
        }
        return builder.build()
    }

}