package com.jasonzyt.mirai.githubbot

import com.google.gson.Gson
import okhttp3.*

object GitHub {
    var auth: String? = null

    class Repo(val name: String) {

        fun getIssue(id: Int): Issue? {
            val resp = httpGet("https://api.github.com/repos/${name}/issues/$id")
            if (resp.code == 200) {
                val json = resp.body.toString()
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
            val resp = httpGet("https://api.github.com/repos/$name/issues/$id")
            if (resp.code == 200) {
                val json = resp.body.toString()
                issueOrPullRequest.pullRequest = try {
                    Gson().fromJson(json, PullRequest::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
            }
            else if (resp.code == 404) {
                val issue = getIssue(id) ?: return null
                issueOrPullRequest.issue = issue
            }
            return null
        }

        fun getStarCount(): Int {
            val resp = httpGet("https://api.github.com/repos/$name")
            if (resp.code == 200) {
                val json = resp.body.toString()
                return try {
                    Gson().fromJson(json, Repository::class.java).stargazers_count
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    -1
                }
            }
            return -1
        }

        fun getCommitCount(): Int {
            val resp = httpGet("https://api.github.com/repos/$name/commits?per_page=1")
            if (resp.code == 200) {
                val link = resp.headers["link"]
                if (link != null) {
                    Regex("<(.+)page=(\\d+)>; rel=\"last\"").find(link)?.let {
                        if (it.groupValues.size == 2) {
                            return@getCommitCount it.groupValues[1].toInt()
                        }
                        return@getCommitCount -1
                    }
                }
            }
            return -1
        }
    }

    fun httpGet(url: String, headersBuilder: Headers.Builder = Headers.Builder()): Response {
        headersBuilder.add("Authorization", auth!!)
        headersBuilder.add("Accept", "application/vnd.github.v3+json")
        val httpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .headers(headersBuilder.build())
            .build()
        return httpClient.newCall(request).execute()
    }

    fun setToken(token: String) {
        val base64 = Utils.base64Encode(token.toByteArray())
        auth = "Basic $base64"
    }

}