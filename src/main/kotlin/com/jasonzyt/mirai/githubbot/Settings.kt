package com.jasonzyt.mirai.githubbot

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Settings : ReadOnlyPluginConfig("settings") {
    @Serializable
    class MatchSettings {
        val regex: String? = null
        val ignoreCase: Boolean = false
        val params: Map<String, Int> = mapOf()
    }
    @Serializable
    class ReplySettings {
        val enabled: Boolean = false
        val message: String? = null
        val matches: List<MatchSettings> = listOf()
    }
    @Serializable
    class ForwardSettings {
        val enabled: Boolean = false
        val message: String? = null
    }
    @Serializable
    class GroupSettings {
        var enabled: Boolean = false
        var webhookRepos: List<String>? = null
        var ignoresMembers: List<Long>? = null
        var forward: Map<String, ForwardSettings>? = null
        var reply: Map<String, ReplySettings>? = null
        var dailySummary: Boolean = true
        val default: Map<String, String>? = null
    }
    @ValueDescription("群聊消息设置")
    val groups: Map<String, GroupSettings> by value()
    @ValueDescription("PAT设置(在这里生成: https://github.com/settings/tokens)")
    val restApiToken: String by value("user:personal-access-token")
    @ValueDescription("Webhook钩子服务器监听端口")
    val webhookPort: Int by value(23333)
    @ValueDescription("Webhook服务器请求路径")
    val webhookPath: String by value("/hooks")
    @ValueDescription("Webhook签名密钥(空字符串代表不使用签名)")
    val webhookSecret: String by value("")
    @ValueDescription("全局默认设置")
    val default: Map<String, String> by value()
    @ValueDescription("全局Webhook事件转发设置")
    val forward: Map<String, ForwardSettings> by value()
    @ValueDescription("全局回复设置(目前仅支持issue, pull_request和discussion)")
    val reply: Map<String, ReplySettings> by value()
    @ValueDescription("全局忽略成员列表(这些成员的发言将不会触发回复)")
    val ignoresMembers: List<Long> by value()
    @ValueDescription("是否启用截图功能(需要前置插件)")
    val enableScreenshot: Boolean by value(true)

    fun getGroupSettings(groupId: Long): GroupSettings? {
        return groups[groupId.toString()]
    }

    fun getGroupReplySettings(groupId: Long): Map<String, ReplySettings> {
        return getGroupSettings(groupId)?.reply ?: reply
    }

}