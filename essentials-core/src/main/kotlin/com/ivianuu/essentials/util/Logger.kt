package com.ivianuu.essentials.util

import com.ivianuu.injekt.Factory
import java.util.regex.Pattern

interface Logger {

    fun v(message: String, tag: String = stackTraceTag)

    fun d(message: String, tag: String = stackTraceTag)

    fun i(message: String, tag: String = stackTraceTag)

    fun w(message: String, tag: String = stackTraceTag)

    fun e(message: String? = null, tag: String = stackTraceTag)

    fun wtf(message: String? = null, tag: String = stackTraceTag)

}

@Factory
object NoopLogger : Logger {
    override fun v(message: String, tag: String) {
    }

    override fun d(message: String, tag: String) {
    }

    override fun i(message: String, tag: String) {
    }

    override fun w(message: String, tag: String) {
    }

    override fun e(message: String?, tag: String) {
    }

    override fun wtf(message: String?, tag: String) {
    }
}

@Factory
class DefaultLogger : Logger {
    override fun v(message: String, tag: String) {
        println("[VERBOSE] $tag $message")
    }

    override fun d(message: String, tag: String) {
        println("[DEBUG] $tag $message")
    }

    override fun i(message: String, tag: String) {
        println("[INFO] $tag $message")
    }

    override fun w(message: String, tag: String) {
        println("[WARN] $tag $message")
    }

    override fun e(message: String?, tag: String) {
        println("[ERROR] $tag $message")
    }

    override fun wtf(message: String?, tag: String) {
        println("[WTF] $tag $message")
    }
}

private val Logger.stackTraceTag: String
    get() = Throwable().stackTrace
        .first {
            it.className != javaClass.canonicalName &&
                    it.className != "com.ivianuu.essentials.util.LoggerKt" &&
                    it.className != "com.ivianuu.essentials.util.Logger\$DefaultImpls"
        }
        .let { createStackElementTag(it) }

private fun createStackElementTag(element: StackTraceElement): String {
    var tag = element.className.substringAfterLast('.')
    val m = ANONYMOUS_CLASS.matcher(tag)
    if (m.find()) {
        tag = m.replaceAll("")
    }
    return tag
}

private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
