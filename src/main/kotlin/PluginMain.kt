package com.jasonzyt.mirai.githubbot

import kotlinx.serialization.Serializable
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.utils.info

class MessageInQueue(
    var group: Long,
    var message: MessageChain
)

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.jasonzyt.mirai.githubbot",
        name = "GitHubBot",
        version = "0.1.0"
    ) {
        author("Jasonzyt")
        info("Powerful GitHubBot by Jasonzyt. GitHub: https://github.com/Jasonzyt/GitHubBot")
    }
) {

    suspend fun sendMessage(group: Long, message: MessageChain) {
        Bot.instances.forEach { bot ->
            bot.getGroup(group)?.sendMessage(message)
        }
    }

    override fun onEnable() {
        logger.info { "Plugin loaded" }
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent>{

        }

    }
}

@Serializable
data class GroupSettings (
    val repositories: Map<String, List<String>>,
    val forwardIssue: Boolean,
    val forwardPullRequest: Boolean,
    val forwardPush: Boolean,
    val forwardDiscussion: Boolean,
    val forwardIssueComment: Boolean,
    val forwardPullRequestComment: Boolean,
    val forwardPushComment: Boolean,
    val forwardDiscussionComment: Boolean,
    val replyIssueOrPullRequest: Boolean,
    val dailySummary: Boolean
)

object Settings : ReadOnlyPluginConfig("settings") {
    val groups: Map<Long, GroupSettings> by value()
    val restApiToken: String by value("user:personal-access-token")
    val webhookPort: Int by value(23333)
    val webhookPath: String by value("/hooks")
    val webhookSecret: String by value("")
}
