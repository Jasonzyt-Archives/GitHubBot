package com.jasonzyt.mirai.githubbot.utils

import okhttp3.internal.format
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class Formatter(private val str: String) {

    private val requirements: MutableList<String> = mutableListOf()
    private val replacements: MutableMap<String, IntRange> = mutableMapOf()
    var result: String = str

    init {
        // Pre-process the string
        val regex = Regex("\\{(.+)}")
        val matches = regex.findAll(str)
        for (match in matches) {
            val key = match.groupValues[1].trim()
            val requirement = key.split('.')[0]
            if (!requirements.contains(requirement)) {
                requirements.add(requirement)
            }
            replacements[key] = match.range
        }
    }

    /**
     * Check if formatting the string requires the given prefix.
     * @param prefix The prefix to check.
     */
    fun require(prefix: String) = requirements.contains(prefix)

    inline fun <reified T> formatWith(v: T, prefix: String = ""): String {
        return this.formatWith(v, T::class, prefix)
    }

    fun <T> formatWith(v: T, clazz: KClass<*>, prefix: String = ""): String {
        for (member in clazz.memberProperties) {
            if (!replacements.contains(prefix + member.name)) {
                continue
            }
            val value = member.getter.call(v)
            if (value != null) {
                result = format(result, value, member.returnType.classifier as KClass<*>, prefix + member.name + ".")
                result = result.replaceRange(replacements[prefix + member.name]!!, value.toString())
            }
        }
        return result
    }

}