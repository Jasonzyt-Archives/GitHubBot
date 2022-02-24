package com.jasonzyt.mirai.githubbot

import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.ConsoleCommandSender

object PluginCommand : CompositeCommand(
    PluginMain, "githubbot", "ghbot", "gh",
    description = "GitHubBot plugin command"
) {
    @SubCommand
    suspend fun ConsoleCommandSender.reload() {
        PluginMain.reloadConfig()
        sendMessage("Config files reloaded!")
    }
}
