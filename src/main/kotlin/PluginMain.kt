package com.jasonzyt.mirai.githubbot

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial

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
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}/"
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent>{
            //群消息
            //复读示例
            if (message.contentToString().startsWith("复读")) {
                group.sendMessage(message.contentToString().replace("复读", ""))
            }
            if (message.contentToString() == "hi") {
                //群内发送
                group.sendMessage("hi")
                //向发送者私聊发送消息
                sender.sendMessage("hi")
                //不继续处理
                return@subscribeAlways
            }
            //分类示例
            message.forEach {
                //循环每个元素在消息里
                if (it is Image) {
                    //如果消息这一部分是图片
                    val url = it.queryUrl()
                    group.sendMessage("图片，下载地址$url")
                }
                if (it is PlainText) {
                    //如果消息这一部分是纯文本
                    group.sendMessage("纯文本，内容:${it.content}")
                }
            }
        }
        eventChannel.subscribeAlways<FriendMessageEvent>{
            //好友信息
            sender.sendMessage("hi")
        }
        eventChannel.subscribeAlways<NewFriendRequestEvent>{
            //自动同意好友申请
            accept()
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent>{
            //自动同意加群申请
            accept()
        }
    }
}

@Serializable
data class GroupSettings (
    val forwardIssue: Boolean,
    val repositories: Map<String, List<String>>,
    val forwardPullRequest: Boolean,
    val forwardPush: Boolean,
    val forwardDiscussion: Boolean,
    val forwardIssueComment: Boolean,
    val forwardPullRequestComment: Boolean,
    val replyIssueOrPullRequest: Boolean,
)

object Settings : ReadOnlyPluginConfig("settings") {
    val groups: Map<Long, GroupSettings> by value()
    val restApiToken: String by value("")
    val webhookPort: Int by value(23333)
    val webhookPath: String by value("/github")
    val webhookSecret: String by value("")
}
