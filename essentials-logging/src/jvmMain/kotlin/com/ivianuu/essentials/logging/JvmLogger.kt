package com.ivianuu.essentials.logging

import java.util.regex.*

actual val Logger.stackTraceTag: String
    get() = Throwable().stackTrace
        .first {
            it.className != javaClass.canonicalName &&
                    it.className != "com.ivianuu.essentials.util.JvmLoggerKt" &&
                    it.className != "com.ivianuu.essentials.Logger\$DefaultImpls"
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
