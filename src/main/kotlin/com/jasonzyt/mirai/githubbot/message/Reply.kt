package com.jasonzyt.mirai.githubbot.message

import com.jasonzyt.mirai.githubbot.PluginMain
import com.jasonzyt.mirai.githubbot.network.GitHub
import com.jasonzyt.mirai.githubbot.utils.Formatter
import com.jasonzyt.mirai.githubbot.utils.Utils
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChainBuilder

abstract class Reply {
    abstract val name: String
    abstract val needs: List<String>
    abstract suspend fun send(contact: Contact)
}

class IssueReply(
    private val message: String,
    private val params: Map<String, String>
) : Reply() {
    override val name: String = "issue"
    override val needs: List<String> = listOf("owner", "repo", "number")
    override suspend fun send(contact: Contact) {
        if (params.keys.containsAll(needs)) {
            val owner = params["owner"]!!
            val name = params["repo"]!!
            val number = params["number"]!!.toInt()
            val issue = GitHub.getIssue(owner, name, number)
            if (issue == null) {
                contact.sendMessage("Issue#$number not found in repo $owner/$name")
            }
            // The first format(text)
            val formatter = Formatter(message)
            if (formatter.require("issue")) {
                formatter.formatWith(issue, "issue.")
            }
            if (formatter.require("repo")) {
                val repo = GitHub.getRepository(owner, name)!!
                formatter.formatWith(repo, "repo.")
            }
            // The second format(media)
            val chainBuilder = MessageChainBuilder()
            contact.sendMessage(Utils.parseMessage(formatter.result, chainBuilder, contact))
        }
        PluginMain.logger.error("Invalid parameters for issue reply, please check your config file.")
    }

}

class PullRequestReply(
    private val message: String,
    private val params: Map<String, String>
) : Reply() {
    override val name: String = "pull_request"
    override val needs: List<String> = listOf("owner", "repo", "number")
    override suspend fun send(contact: Contact) {
        if (params.keys.containsAll(needs)) {
            val owner = params["owner"]!!
            val name = params["repo"]!!
            val number = params["number"]!!.toInt()
            val pullRequest = GitHub.getPullRequest(owner, name, number)
            if (pullRequest == null) {
                contact.sendMessage("PullRequest#$number not found in repo $owner/$name")
            }
            // The first format(text)
            val formatter = Formatter(message)
            if (formatter.require("pull_request")) {
                formatter.formatWith(pullRequest, "pull_request.")
            }
            if (formatter.require("repo")) {
                val repo = GitHub.getRepository(owner, name)!!
                formatter.formatWith(repo, "repo.")
            }
            // The second format(media)
            val chainBuilder = MessageChainBuilder()
            contact.sendMessage(Utils.parseMessage(formatter.result, chainBuilder, contact))
        }
        PluginMain.logger.error("Invalid parameters for pull request reply, please check your config file.")
    }
}

class DiscussionReply(
    private val message: String,
    private val params: Map<String, String>
) : Reply() {
    override val name: String = "discussion"
    override val needs: List<String> = listOf("owner", "repo", "number")
    override suspend fun send(contact: Contact) {
        if (params.keys.containsAll(needs)) {
            val owner = params["owner"]!!
            val name = params["repo"]!!
            val number = params["number"]!!.toInt()
            val discussion = GitHub.getDiscussion(owner, name, number)
            if (discussion == null) {
                contact.sendMessage("Discussion#$number not found in repo $owner/$name")
            }
            // The first format(text)
            val formatter = Formatter(message)
            if (formatter.require("discussion")) {
                formatter.formatWith(discussion, "discussion.")
            }
            if (formatter.require("repo")) {
                val repo = GitHub.getRepository(owner, name)!!
                formatter.formatWith(repo, "repo.")
            }
            // The second format(media)
            val chainBuilder = MessageChainBuilder()
            contact.sendMessage(Utils.parseMessage(formatter.result, chainBuilder, contact))
        }
        PluginMain.logger.error("Invalid parameters for discussion reply, please check your config file.")
    }
}

class CommunicationReply(
    private val messages: Map<String, String>, // It should contain issue, pull_request, discussion
    private val params: Map<String, String>
) : Reply() {
    override val name: String = "communication"
    override val needs: List<String> = listOf("owner", "repo", "number")
    override suspend fun send(contact: Contact) {
        if (params.keys.containsAll(needs)) {
            val owner = params["owner"]!!
            val name = params["repo"]!!
            val number = params["number"]!!.toInt()
            val communication = GitHub.getCommunication(owner, name, number)
            if (!communication.isValid()) {
                contact.sendMessage("#$number not found in repo $owner/$name")
            }
            var formatter: Formatter? = null
            if (communication.isIssue()) {
                formatter = Formatter(messages["issue"]!!)
                if (formatter.require("issue")) {
                    formatter.formatWith(communication.issue, "issue.")
                }
            } else if (communication.isPullRequest()) {
                formatter = Formatter(messages["pull_request"]!!)
                if (formatter.require("pull_request")) {
                    formatter.formatWith(communication.pullRequest, "pull_request.")
                }
            } else if (communication.isDiscussion()) {
                formatter = Formatter(messages["discussion"]!!)
                if (formatter.require("discussion")) {
                    formatter.formatWith(communication.discussion, "discussion.")
                }
            }
            if (formatter == null) {
                contact.sendMessage("#$number not found in repo $owner/$name")
                return
            }
            if (formatter.require("repo")) {
                val repo = GitHub.getRepository(owner, name)!!
                formatter.formatWith(repo, "repo.")
            }
            // The second format(media)
            val chainBuilder = MessageChainBuilder()
            contact.sendMessage(Utils.parseMessage(formatter.result, chainBuilder, contact))
        }
        PluginMain.logger.error("Invalid parameters for communication reply, please check your config file.")
    }
}

class RepositoryReply(
    private val message: String,
    private val params: Map<String, String>
) : Reply() {
    override val name: String = "repository"
    override val needs: List<String> = listOf("owner", "repo")
    override suspend fun send(contact: Contact) {
        if (params.keys.containsAll(needs)) {
            val owner = params["owner"]!!
            val name = params["repo"]!!
            val repo = GitHub.getRepository(owner, name)!!
            // The first format(text)
            val formatter = Formatter(message)
            if (formatter.require("repo")) {
                formatter.formatWith(repo, "repo.")
            }
            // The second format(media)
            val chainBuilder = MessageChainBuilder()
            contact.sendMessage(Utils.parseMessage(formatter.result, chainBuilder, contact))
        }
        PluginMain.logger.error("Invalid parameters for repository reply, please check your config file.")
    }
}

