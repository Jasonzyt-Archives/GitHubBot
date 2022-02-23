package com.jasonzyt.mirai.githubbot.selenium

import com.jasonzyt.mirai.githubbot.PluginMain
import com.jasonzyt.mirai.githubbot.Settings
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebDriver
import xyz.cssxsh.mirai.plugin.MiraiSeleniumPlugin
import xyz.cssxsh.selenium.isReady

class Selenium(url: String) {
    init {
        if (driver.currentUrl != url)
            driver.get(url)
    }

    fun findtElementsByCssSelector(cssSelector: String): List<WebElement>? {
        return driver.findElements(By.cssSelector(cssSelector))
    }
    fun findElementByAttribute(attribute: String, value: String): WebElement? {
        return driver.findElement(By.cssSelector("[$attribute='$value']"))
    }
    fun findElements(by: By): List<WebElement>? {
        return driver.findElements(by)
    }
    fun findElement(by: By): WebElement? {
        return driver.findElement(by)
    }

    var windowSize: Dimension
        get() = driver.manage().window().size
        set(value) {
            driver.manage().window().size = value
        }

    suspend fun waitForReady(timeout: Long = 10000) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeout) {
            if (driver.isReady()) {
                return
            }
            delay(100)
        }
    }

    fun close() = driver.close()

    companion object {
        var has: Boolean = false
        lateinit var driver: RemoteWebDriver

        private fun check(): Boolean {
            if (!Settings.enableScreenshot) return false
            return try {
                has = MiraiSeleniumPlugin.setup()
                has
            } catch (exception: NoClassDefFoundError) {
                PluginMain.logger.warning("相关类加载失败! 请安装 https://github.com/cssxsh/mirai-selenium-plugin $exception")
                false
            }
        }

        fun init() {
            check()
            if (has) {
                driver = MiraiSeleniumPlugin.driver(config = SeleniumConfig)
            }
        }

        fun quit() {
            if (has) {
                driver.quit()
            }
        }
    }
}