package com.jasonzyt.mirai.githubbot

import org.rapidoid.http.ReqHandler
import org.rapidoid.http.Resp
import org.rapidoid.http.customize.ErrorHandler
import org.rapidoid.setup.My
import org.rapidoid.setup.On

object Webhook {

    private fun checkSignature(text: String, signature: String): Boolean {
        if (Settings.webhookSecret.isEmpty()) {
            return true
        }
        val bytes = Utils.HMacSha256(text.toByteArray(), Settings.webhookSecret.toByteArray())
        return signature == "sha256=" + Utils.byte2hex(bytes)
    }

    private val errorHandler = ErrorHandler { _, resp: Resp, e: Throwable ->
        resp.code(500).body("Internal Server Error".toByteArray())
        PluginMain.logger.error("Webhook: A exception has occurred!")
        PluginMain.logger.error(e)
    }

    private val reqHandler = ReqHandler { req ->
        val resp = req.response()
        PluginMain.logger.info("Webhook: Received a request from ${req.clientIpAddress()}")
        if (req.path() == Settings.webhookPath) {
            if (checkSignature(req.body().toString(), req.header("X-Hub-Signature-256").toString())) {
                val event = req.header("X-GitHub-Event").toString()
                when (EventType.value(event)) {

                }
            } else {
                PluginMain.logger.warning("Webhook: Invalid signature. Ignored.")
                resp.code(403) // Forbidden
            }
        }
        return@ReqHandler resp
    }

    fun start() {
        My.errorHandler(errorHandler)
        On.port(Settings.webhookPort)
        On.post(Settings.webhookPath).plain(reqHandler)
        PluginMain.logger.info("Webhook(HTTPServer) started")
    }

}