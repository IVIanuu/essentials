/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import com.ivianuu.essentials.util.Logger.Kind.DEBUG
import com.ivianuu.essentials.util.Logger.Kind.ERROR
import com.ivianuu.essentials.util.Logger.Kind.INFO
import com.ivianuu.essentials.util.Logger.Kind.VERBOSE
import com.ivianuu.essentials.util.Logger.Kind.WARN
import com.ivianuu.essentials.util.Logger.Kind.WTF
import com.ivianuu.injekt.Binding
import java.util.regex.Pattern

interface Logger {
    val isEnabled: Boolean

    fun log(
        kind: Kind,
        message: String? = null,
        throwable: Throwable? = null,
        tag: String? = null,
    )

    enum class Kind {
        VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
    }
}

inline fun Logger.v(
    tag: String? = null,
    throwable: Throwable? = null,
    message: () -> String? = { null },
) {
    log(VERBOSE, throwable, tag, message)
}

inline fun Logger.d(
    throwable: Throwable? = null,
    tag: String? = null,
    message: () -> String? = { null },
) {
    log(DEBUG, throwable, tag, message)
}

inline fun Logger.i(
    throwable: Throwable? = null,
    tag: String? = null,
    message: () -> String? = { null },
) {
    log(INFO, throwable, tag, message)
}

inline fun Logger.w(
    throwable: Throwable? = null,
    tag: String? = null,
    message: () -> String? = { null },
) {
    log(WARN, throwable, tag, message)
}

inline fun Logger.e(
    throwable: Throwable? = null,
    tag: String? = null,
    message: () -> String? = { null },
) {
    log(ERROR, throwable, tag, message)
}

inline fun Logger.wtf(
    throwable: Throwable? = null,
    tag: String? = null,
    message: () -> String? = { null },
) {
    log(WTF, throwable, tag, message)
}

inline fun Logger.log(
    kind: Logger.Kind,
    throwable: Throwable? = null,
    tag: String? = null,
    message: () -> String? = { null },
) {
    if (isEnabled) log(kind, message(), throwable, tag)
}

inline fun Logger.warn(
    tag: String? = null,
    throwable: Throwable? = null,
    message: () -> String? = { null },
) {
    log(WARN, throwable, tag, message)
}

@Binding
object NoopLogger : Logger {
    override val isEnabled: Boolean
        get() = false

    override fun log(kind: Logger.Kind, message: String?, throwable: Throwable?, tag: String?) {
    }
}

@Binding
class DefaultLogger(override val isEnabled: LoggingEnabled) : Logger {
    override fun log(kind: Logger.Kind, message: String?, throwable: Throwable?, tag: String?) {
        println("[${kind.name}]${tag ?: stackTraceTag} ${render(message, throwable)}")
    }

    private fun render(message: String?, throwable: Throwable?) {
        buildString {
            append(message.orEmpty())
            if (throwable != null) {
                append(" ")
            }
            append(throwable?.toString().orEmpty())
        }
    }
}

val Logger.stackTraceTag: String
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

typealias LoggingEnabled = Boolean
