package com.jasonzyt.mirai.githubbot

import com.google.gson.Gson
import okhttp3.*

object GitHub {
    var auth: String? = null

    class Communication {
        var pullRequest: PullRequest? = null
        var issue: Issue? = null
        var discussion: Discussion? = null

        fun isPullRequest(): Boolean {
            return pullRequest != null
        }

        fun isIssue(): Boolean {
            return issue != null
        }

        fun isDiscussion(): Boolean {
            return discussion != null
        }

        fun isValid(): Boolean {
            return isPullRequest() || isIssue() || isDiscussion()
        }
    }

    class Repo(val name: String) {

        fun getIssue(num: Int): Issue? {
            var issue: Issue? = Issue()
            val resp = httpGet("https://api.github.com/repos/$name/issues/$num")
            if (resp.code == 200) {
                val json = resp.body?.string()
                issue = try {
                    Gson().fromJson(json, Issue::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
            }
            resp.close()
            return issue
        }

        fun getPullRequest(num: Int): PullRequest? {
            var pullRequest: PullRequest? = PullRequest()
            val resp = httpGet("https://api.github.com/repos/$name/pulls/$num")
            if (resp.code == 200) {
                val json = resp.body?.string()
                pullRequest = try {
                    Gson().fromJson(json, PullRequest::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
            }
            resp.close()
            return pullRequest
        }

        fun getDiscussion(num: Int): Discussion? {
            var discussion: Discussion? = Discussion()
            val resp = httpGet("https://api.github.com/repos/$name/issues/$num")
            if (resp.code == 200) {
                val json = resp.body?.string()
                discussion = try {
                    Gson().fromJson(json, Discussion::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
            }
            resp.close()
            return discussion
        }

        fun getCommunication(num: Int): Communication {
            val communication = Communication()
            communication.pullRequest = getPullRequest(num)
            if (communication.pullRequest != null) return communication
            communication.discussion = getDiscussion(num)
            if (communication.discussion != null) return communication
            communication.issue = getIssue(num)
            return communication
        }

        fun getStarCount(): Int {
            val resp = httpGet("https://api.github.com/repos/$name")
            if (resp.code == 200) {
                val json = resp.body?.charStream()?.readText()
                resp.close()
                return try {
                    Gson().fromJson(json, Repository::class.java).stargazers_count
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    -1
                }
            }
            resp.close()
            return -1
        }

        fun getCommitCount(): Int {
            val resp = httpGet("https://api.github.com/repos/$name/commits?per_page=1")
            if (resp.code == 200) {
                val link = resp.headers["link"]
                resp.close()
                if (link != null) {
                    Regex("<(.+)page=(\\d+)>; rel=\"last\"").find(link)?.let {
                        if (it.groupValues.size == 2) {
                            return@getCommitCount it.groupValues[1].toInt()
                        }
                        return@getCommitCount -1
                    }
                }
            }
            resp.close()
            return -1
        }
    }

    fun httpGet(url: String, headersBuilder: Headers.Builder = Headers.Builder()): Response {
        if (auth != null) headersBuilder.add("Authorization", auth!!)
        headersBuilder.add("Accept", "application/vnd.github.v3+json")
        val httpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .headers(headersBuilder.build())
            .build()
        return httpClient.newCall(request).execute()
    }

    fun setToken(token: String) {
        if (token.isEmpty()) {
            return
        }
        val base64 = Utils.base64Encode(token.toByteArray())
        auth = "Basic $base64"
    }

}