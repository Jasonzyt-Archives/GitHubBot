package com.jasonzyt.mirai.githubbot

import com.google.gson.Gson
import com.github.kevinsawicki.http.HttpRequest

object GitHub {
    var auth: String? = null

    class Repo(val name: String) {

        fun getIssue(id: Int): Issue? {
            val resp = HttpRequest.get("https://api.github.com/repos/$name/issues/$id")
                .header("Authorization", auth!!)
            if (resp.code() == 200) {
                val json = resp.body()
                return try {
                    Gson().fromJson(json, Issue::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
            }
            return null
        }

        fun getIssueOrPullRequest(id: Int): IssueOrPullRequest? {
            val issueOrPullRequest = IssueOrPullRequest()
            val resp = HttpRequest.get("https://api.github.com/repos/$name/pulls/$id")
                .header("Authorization", auth!!)
            if (resp.code() == 200) {
                val json = resp.body()
                issueOrPullRequest.pullRequest = try {
                    Gson().fromJson(json, PullRequest::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
            }
            else if (resp.code() == 404) {
                val issue = getIssue(id) ?: return null
                issueOrPullRequest.issue = issue
            }
            return null
        }

        fun getStarCount(): Int {
            val resp = HttpRequest.get("https://api.github.com/repos/$name")
                .header("Authorization", auth!!)
            if (resp.code() == 200) {
                val json = resp.body()
                return try {
                    Gson().fromJson(json, Repository::class.java).stargazers_count
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    0
                }
            }
            return 0
        }
    }

    fun setToken(token: String) {
        val base64 = Utils.base64Encode(token.toByteArray())
        auth = "Basic $base64"
    }

}