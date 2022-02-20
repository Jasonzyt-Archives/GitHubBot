package com.jasonzyt.mirai.githubbot.selenium

import com.jasonzyt.mirai.githubbot.PluginMain
import com.jasonzyt.mirai.githubbot.Settings
import org.openqa.selenium.remote.RemoteWebDriver
import xyz.cssxsh.mirai.plugin.MiraiSeleniumPlugin

object Selenium {
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