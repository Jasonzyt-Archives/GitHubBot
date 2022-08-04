package com.jasonzyt.mirai.githubbot.network

import com.jasonzyt.mirai.githubbot.Event
import com.jasonzyt.mirai.githubbot.EventType
import com.jasonzyt.mirai.githubbot.PluginMain
import com.jasonzyt.mirai.githubbot.Settings
import com.jasonzyt.mirai.githubbot.utils.Utils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Webhook {

    private fun checkSignature(text: String, signature: String): Boolean {
        if (Settings.webhookSecret.isEmpty()) {
            return true
        }
        val bytes = Utils.doHMacSha256(text.toByteArray(), Settings.webhookSecret.toByteArray())
        PluginMain.logger.info("byteArr: $bytes")
        PluginMain.logger.info("signature: $signature")
        PluginMain.logger.info("bytes: ${Utils.byte2hex(bytes)}")
        return signature == "sha256=" + Utils.byte2hex(bytes)
    }

    fun start() {
        CoroutineScope(PluginMain.coroutineContext).launch {
            embeddedServer(Netty, port = Settings.webhookPort) {
                routing {
                    post(Settings.webhookPath) {
                        val signature = call.request.headers["X-Hub-Signature-256"]
                        val body = call.receiveText()
                        PluginMain.logger.info("Received post request from ${call.request.host()}")
                        PluginMain.logger.info("body: $body")
                        if (signature != null && checkSignature(body, signature)) {
                            val eventStr = call.request.headers["X-GitHub-Event"]
                            if (eventStr == null) {
                                call.respond(HttpStatusCode.BadRequest, "Missing X-GitHub-Event header")
                                return@post
                            }
                            val eventType = EventType.value(eventStr)
                            val event = Event.fromJson(body)
                            val guid = call.request.headers["X-GitHub-Delivery"]
                            if (event == null) {
                                call.respond(HttpStatusCode.InternalServerError, "Parse JSON failed")
                                return@post
                            }
                            event.type = eventType
                            event.guid = guid
                            when (eventType) {
                                EventType.Issues -> {
                                    PluginMain.addMessage(
                                        788712885,
                                        "Webhook: Issue#${event.issue?.number} Action: ${event.action}"
                                    )
                                }
                            }
                        } else {
                            PluginMain.logger.warning("Webhook: Invalid signature. Ignored.")
                            call.respond(HttpStatusCode.Forbidden, "Invalid signature")
                        }
                    }
                }
            }.start(wait = true)
        }
        PluginMain.logger.info("Webhook(HTTPServer) started")
    }

}