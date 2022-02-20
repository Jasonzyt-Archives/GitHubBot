package com.jasonzyt.mirai.githubbot.selenium

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.selenium.*

// From https://github.com/cssxsh/mirai-selenium-plugin/blob/master/src/test/kotlin/xyz/cssxsh/mirai/plugin/SeleniumConfig.kt
object SeleniumConfig : ReadOnlyPluginConfig("selenium"), RemoteWebDriverConfig {
    @ValueName("user_agent")
    @ValueDescription("截图UA")
    override val userAgent: String by value(UserAgents.IPAD)

    @ValueName("width")
    @ValueDescription("截图宽度")
    override val width: Int by value(768)

    @ValueName("height")
    @ValueDescription("截图高度")
    override val height: Int by value(1024)

    @ValueName("headless")
    @ValueDescription("无头模式(后台模式)")
    override val headless: Boolean by value(true)

    @ValueName("browser")
    @ValueDescription("指定使用的浏览器,Chrome/Firefox")
    override val browser: String by value("")

    @ValueName("factory")
    @ValueDescription("指定使用的Factory")
    override val factory: String by value("ktor")
}