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

        fun getCommunication(id: Int): Communication {
            val communication = Communication()
            var resp = httpGet("https://api.github.com/repos/$name/pulls/$id")
            if (resp.code == 200) {
                val json = resp.body?.string()
                communication.pullRequest = try {
                    Gson().fromJson(json, PullRequest::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
                resp.close()
                return communication
            }
            resp.close()
            resp = httpGet("https://api.github.com/repos/$name/discussions/$id")
            if (resp.code == 200) {
                val json = resp.body?.string()
                communication.discussion = try {
                    Gson().fromJson(json, Discussion::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
                resp.close()
                return communication
            }
            resp.close()
            resp = httpGet("https://api.github.com/repos/$name/issues/$id")
            if (resp.code == 200) {
                val json = resp.body?.string()
                communication.issue = try {
                    Gson().fromJson(json, Issue::class.java)
                } catch (e: Exception) {
                    PluginMain.logger.error("Failed to parse event JSON: $json")
                    PluginMain.logger.error(e)
                    null
                }
                resp.close()
                return communication
            }
            resp.close()
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