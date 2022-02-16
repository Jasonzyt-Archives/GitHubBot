package com.jasonzyt.mirai.githubbot

import net.mamoe.mirai.console.data.AutoSavePluginData

object Statistics : AutoSavePluginData("statistics") {

    class User {
        val commits: Map<String, Int> = HashMap()
        val issues: Map<String, Int> = HashMap()
        val pullRequests: Map<String, Int> = HashMap()
        val comments: Map<String, Int> = HashMap()
    }

}