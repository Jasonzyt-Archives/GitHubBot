package com.jasonzyt.mirai.githubbot

import com.jasonzyt.mirai.githubbot.selenium.Selenium
import com.jasonzyt.mirai.githubbot.selenium.SeleniumConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import java.io.File
import java.util.*

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.jasonzyt.mirai.githubbot",
        name = "GitHubBot",
        version = "0.1.0"
    ) {
        author("Jasonzyt")
        info("Powerful GitHubBot by Jasonzyt. GitHub: https://github.com/Jasonzyt/GitHubBot")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-selenium-plugin", true)
    }
) {

    class MessageInQueue(
        var group: Long,
        var message: MessageChain
    )

    private val msgQueue = mutableListOf<MessageInQueue>()

    fun addMessage(group: Long, message: MessageChain) {
        msgQueue.add(MessageInQueue(group, message))
    }

    fun addMessage(group: Long, message: String) {
        val builder = MessageChainBuilder()
        builder.add(message)
        addMessage(group, builder.asMessageChain())
    }

    private fun startSending() {
        CoroutineScope(coroutineContext).launch {
            while (true) {
                if (msgQueue.isNotEmpty()) {
                    val msg = msgQueue.removeAt(0)
                    sendMessage(msg.group, msg.message)
                }
                delay(1000)
            }
        }
    }

    suspend fun sendMessage(group: Long, message: MessageChain) {
        Bot.instances.forEach { bot ->
            val g = bot.getGroup(group)
            if (g != null) {
                g.sendMessage(message)
                return
            }
        }
    }

    override fun onEnable() {
        logger.info("GitHubBot loaded! Author: Jasonzyt")
        logger.info("GitHub Repository: https://github.com/Jasonzyt/GitHubBot")
        //Logger.getLogger(okhttp3.OkHttpClient::class.java.name).level = Level.OFF
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent>{ ev ->
            val groupSettings = Settings.getGroupSettings(ev.group.id)
            val defaultRepo = Settings.getGroupDefaultRepo(ev.group.id)
            if (
                groupSettings == null ||
                ev.bot.id == ev.sender.id ||
                defaultRepo == null ||
                groupSettings.ignoresMembers?.contains(ev.sender.id) == true ||
                Settings.ignoresMembers.contains(ev.sender.id)
            ) {
                return@subscribeAlways
            }

            if (groupSettings.enabled && groupSettings.defaultRepo?.isNotEmpty() == true) {
                val results = Regex("#([0-9]+)").findAll(ev.message.contentToString())
                for (result in results) {
                    val number = result.value.drop(1).toInt()
                    val communication = GitHub.Repo(defaultRepo).getCommunication(number)
                    if (communication.isIssue()) {
                        val replySettings = Settings.getGroupReplySettings(ev.group.id, "issue")
                        if (replySettings != null && replySettings.enabled && replySettings.message != null) {
                            val image = communication.issue?.let { Utils.getIssueScreenshot(it.html_url, it.node_id) }
                            if (image == null) {
                                ev.group.sendMessage("Cant get issue screenshot")
                                return@subscribeAlways
                            }
                            val builder = MessageChainBuilder()
                            builder.append(Utils.format(replySettings.message, communication.issue))
                            File.createTempFile("com_jasonzyt_mirai_githubbot_", ".png").apply {
                                writeBytes(image)
                            }.let {
                                builder.append(group.uploadImage(it))
                            }
                            ev.group.sendMessage(builder.build())
                        }
                    } else if (communication.isPullRequest()) {
                        val replySettings = Settings.getGroupReplySettings(ev.group.id, "pull_request")
                        if (replySettings != null && replySettings.enabled && replySettings.message != null) {
                            ev.group.sendMessage(Utils.format(replySettings.message, communication.pullRequest))
                        }
                    } else if (communication.isDiscussion()) {
                        val replySettings = Settings.getGroupReplySettings(ev.group.id, "discussion")
                        if (replySettings != null && replySettings.enabled && replySettings.message != null) {
                            ev.group.sendMessage(Utils.format(replySettings.message, communication.discussion))
                        }
                    }
                }
            }
        }
        Settings.reload()
        SeleniumConfig.reload()
        GitHub.setToken(Settings.restApiToken)
        Selenium.init()
        startSending()
        //Webhook.start()
    }

    override fun onDisable() {
        Selenium.quit()
    }
}
