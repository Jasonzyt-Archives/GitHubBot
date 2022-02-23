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
        eventChannel.subscribeAlways<GroupMessageEvent> { ev ->
            val groupSettings = Settings.getGroupSettings(ev.group.id)
            var defaultRepo = Settings.getGroupDefaultRepo(ev.group.id)
            if (
                groupSettings == null ||
                ev.bot.id == ev.sender.id ||
                groupSettings.ignoresMembers?.contains(ev.sender.id) == true ||
                Settings.ignoresMembers.contains(ev.sender.id) ||
                !groupSettings.enabled ||
                groupSettings.reply == null
            ) {
                return@subscribeAlways
            }
            if (defaultRepo?.isEmpty() == true) defaultRepo = null

            val replySettings = groupSettings.reply

            // todo: Don't require default repo
            if (defaultRepo != null) {
                val found = mutableListOf<Int>()
                if (replySettings["pull_request"]?.enabled == true) {
                    for (m in replySettings["pull_request"]!!.matches) {
                        m.regex.toRegex().findAll(ev.message.contentToString()).forEach {
                            val index = m.keys["number"] ?: 1
                            if (index >= 0 && index < it.groupValues.size) {
                                val num = it.groupValues[index].toInt()
                                if (found.contains(num)) return@forEach
                                val pullRequest = GitHub.Repo(defaultRepo).getPullRequest(num)
                                if (pullRequest != null) {
                                    found.add(num)
                                    val str = Utils.format(replySettings["pull_request"]?.message ?: "", pullRequest)
                                    ev.group.sendMessage(Utils.buildImageMessage(str, ev.group))
                                }
                            }
                        }
                    }
                }
                if (replySettings["discussion"]?.enabled == true) {
                    for (m in replySettings["discussion"]!!.matches) {
                        m.regex.toRegex().findAll(ev.message.contentToString()).forEach {
                            val index = m.keys["number"] ?: 1
                            if (index >= 0 && index < it.groupValues.size) {
                                val num = it.groupValues[index].toInt()
                                if (found.contains(num)) return@forEach
                                val discussion = GitHub.Repo(defaultRepo).getDiscussion(num)
                                if (discussion != null) {
                                    found.add(num)
                                    val str = Utils.format(replySettings["discussion"]?.message ?: "", discussion)
                                    ev.group.sendMessage(Utils.buildImageMessage(str, ev.group))
                                }
                            }
                        }
                    }
                }
                if (replySettings["issue"]?.enabled == true) {
                    for (m in replySettings["issue"]!!.matches) {
                        m.regex.toRegex().findAll(ev.message.contentToString()).forEach {
                            val index = m.keys["number"] ?: 1
                            if (index >= 0 && index < it.groupValues.size) {
                                val num = it.groupValues[index].toInt()
                                if (found.contains(num)) return@forEach
                                val issue = GitHub.Repo(defaultRepo).getIssue(num)
                                if (issue != null) {
                                    found.add(num)
                                    val str = Utils.format(replySettings["issue"]?.message ?: "", issue)
                                    ev.group.sendMessage(Utils.buildImageMessage(str, ev.group))
                                }
                            }
                        }
                    }
                }
            }

            if (replySettings["repository"]?.enabled == true) {
                for (m in replySettings["repository"]!!.matches) {
                    m.regex.toRegex().findAll(ev.message.contentToString()).forEach {
                        val ownerIndex = m.keys["owner"] ?: 1
                        val nameIndex = m.keys["name"] ?: 2
                        if (ownerIndex >= 0 && ownerIndex < it.groupValues.size &&
                            nameIndex >= 0 && nameIndex < it.groupValues.size) {
                            val owner = it.groupValues[ownerIndex]
                            val name = it.groupValues[nameIndex]
                            val repo = GitHub.getRepository(owner, name)
                            if (repo != null) {
                                val str = Utils.format(replySettings["repository"]?.message ?: "", repo)
                                ev.group.sendMessage(Utils.buildImageMessage(str, ev.group))
                            }
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
