package com.jasonzyt.mirai.githubbot

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
        embeddedServer(Netty, port = Settings.webhookPort) {
            routing {
                post(Settings.webhookPath) {
                    val signature = call.request.headers["X-Hub-Signature-256"]
                    if (signature != null && checkSignature(call.receiveText(), signature)) {
                        val eventStr = call.request.headers["X-GitHub-Event"]
                        if (eventStr == null) {
                            call.respond(HttpStatusCode.BadRequest, "Missing X-GitHub-Event header")
                            return@post
                        }
                        val eventType = EventType.value(eventStr)
                        val json = call.receiveText()
                        val event = Event.fromJson(json)
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
        PluginMain.logger.info("Webhook(HTTPServer) started")
    }

}