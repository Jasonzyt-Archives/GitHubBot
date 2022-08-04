package com.jasonzyt.mirai.githubbot.network

import com.google.gson.Gson
import com.jasonzyt.mirai.githubbot.*
import com.jasonzyt.mirai.githubbot.utils.Utils
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object GitHub {
    private var client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
        defaultRequest {
            url("https://api.github.com")
            header("Accept", "application/vnd.github.v3+json")
        }
    }

    fun setAuthorization(auth: String) {
        val authEncoded = Utils.base64Encode(auth.toByteArray())
        client = client.config {
            defaultRequest {
                header(HttpHeaders.Authorization, "Basic $authEncoded")
            }
        }
    }

    class Communication {
        var pullRequest: PullRequest? = null
        var issue: Issue? = null
        var discussion: Discussion? = null

        fun isPullRequest() = pullRequest != null

        fun isIssue() = issue != null

        fun isDiscussion() = discussion != null

        fun isValid() = isPullRequest() || isIssue() || isDiscussion()
    }


    suspend fun getIssue(owner: String, repo: String, number: Int): Issue? {
        val resp = client.get("/repos/$owner/$repo/issues/$number")
        if (resp.status == HttpStatusCode.OK) {
            val json = resp.bodyAsText()
            return try {
                Gson().fromJson(json, Issue::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse JSON: $json")
                PluginMain.logger.error(e)
                null
            }
        }
        return null
    }

    suspend fun getPullRequest(owner: String, repo: String, number: Int): PullRequest? {
        val resp = client.get("/repos/$owner/$repo/pulls/$number")
        if (resp.status == HttpStatusCode.OK) {
            val json = resp.bodyAsText()
            return try {
                Gson().fromJson(json, PullRequest::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse JSON: $json")
                PluginMain.logger.error(e)
                null
            }
        }
        return null
    }

    suspend fun getDiscussion(owner: String, repo: String, number: Int): Discussion? {
        val resp = client.get("/repos/$owner/$repo/discussions/$number")
        if (resp.status == HttpStatusCode.OK) {
            val json = resp.bodyAsText()
            return try {
                Gson().fromJson(json, Discussion::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse JSON: $json")
                PluginMain.logger.error(e)
                null
            }
        }
        return null
    }

    suspend fun getRepository(owner: String, repo: String): Repository? {
        val resp = client.get("/repos/$owner/$repo")
        if (resp.status == HttpStatusCode.OK) {
            val json = resp.bodyAsText()
            return try {
                Gson().fromJson(json, Repository::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse JSON: $json")
                PluginMain.logger.error(e)
                null
            }
        }
        return null
    }

    suspend fun getCommunication(owner: String, repo: String, id: Int): Communication {
        val communication = Communication()
        var resp = client.get("/repos/$owner/$repo/pulls/$id")
        if (resp.status == HttpStatusCode.OK) {
            val json = resp.bodyAsText()
            communication.pullRequest = try {
                Gson().fromJson(json, PullRequest::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse JSON: $json")
                PluginMain.logger.error(e)
                null
            }
            return communication
        }
        resp = client.get("/repos/$owner/$repo/discussions/$id")
        if (resp.status == HttpStatusCode.OK) {
            val json = resp.bodyAsText()
            communication.discussion = try {
                Gson().fromJson(json, Discussion::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse JSON: $json")
                PluginMain.logger.error(e)
                null
            }
            return communication
        }
        resp = client.get("/repos/$owner/$repo/issues/$id")
        if (resp.status == HttpStatusCode.OK) {
            val json = resp.bodyAsText()
            communication.issue = try {
                Gson().fromJson(json, Issue::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse JSON: $json")
                PluginMain.logger.error(e)
                null
            }
            return communication
        }
        return communication
    }

    class Repo(private val fullName: String) {

        suspend fun getCommitCount(): Int {
            val resp = client.get("/repos/$fullName/commits?per_page=1")
            if (resp.status == HttpStatusCode.OK) {
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

}