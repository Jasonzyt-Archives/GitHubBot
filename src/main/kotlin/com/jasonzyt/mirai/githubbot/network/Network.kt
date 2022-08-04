package com.jasonzyt.mirai.githubbot.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Network {

    private val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
    }

    suspend fun getImage(url: String): File? {
        val resp = client.request(url) {
            method = HttpMethod.Get
            accept(ContentType.Image.Any)
        }
        if (resp.status == HttpStatusCode.OK) {
            val file = withContext(Dispatchers.IO) {
                File.createTempFile("com_jasonzyt_mirai_githubbot_", "")
            }
            file.writeBytes(resp.readBytes())
            return file
        }
        return null
    }

}