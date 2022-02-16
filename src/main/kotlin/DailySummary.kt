package com.jasonzyt.mirai.githubbot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*

// 每日总结
object DailySummary {

    var scope: CoroutineScope? = null

    fun start() {
        scope = CoroutineScope(PluginMain.coroutineContext)
        scope!!.launch {
            while (true) {
                val time = LocalTime.now()
                if (time.hour == 0 && time.minute == 0) {

                }
                delay(1000 * 60) // 60s
            }
        }
    }

}