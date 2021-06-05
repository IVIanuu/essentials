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

package com.ivianuu.essentials.logging

import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.Logger.Kind.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

interface Logger {
  val isEnabled: Boolean

  fun log(
    kind: Kind,
    tag: LoggingTag,
    message: String? = null,
    throwable: Throwable? = null
  )

  enum class Kind {
    VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
  }
}

inline fun v(
  throwable: Throwable? = null,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String? = { null },
) {
  log(kind = VERBOSE, throwable = throwable, message = message)
}

inline fun d(
  throwable: Throwable? = null,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String? = { null },
) {
  log(kind = DEBUG, throwable = throwable, message = message)
}

inline fun i(
  throwable: Throwable? = null,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String? = { null },
) {
  log(kind = INFO, throwable = throwable, message = message)
}

inline fun w(
  throwable: Throwable? = null,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String? = { null },
) {
  log(kind = WARN, throwable = throwable, message = message)
}

inline fun e(
  throwable: Throwable? = null,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String? = { null },
) {
  log(kind = ERROR, throwable = throwable, message = message)
}

inline fun wtf(
  throwable: Throwable? = null,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String? = { null },
) {
  log(kind = WTF, throwable = throwable, message = message)
}

inline fun log(
  kind: Logger.Kind,
  throwable: Throwable? = null,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String? = { null },
) {
  if (logger.isEnabled) logger.log(kind, tag, message(), throwable)
}

@Provide @Factory object NoopLogger : Logger {
  override val isEnabled: Boolean
    get() = false

  override fun log(kind: Logger.Kind, tag: LoggingTag, message: String?, throwable: Throwable?) {
  }
}

@Provide @Factory class PrintingLogger(override val isEnabled: LoggingEnabled) : Logger {
  override fun log(kind: Logger.Kind, tag: LoggingTag, message: String?, throwable: Throwable?) {
    println("[${kind.name}] $tag ${render(message, throwable)}")
  }

  private fun render(message: String?, throwable: Throwable?) = buildString {
    append(message.orEmpty())
    if (throwable != null) {
      append(" ")
    }
    append(throwable?.toString().orEmpty())
  }
}

typealias LoggingTag = String

@Provide fun loggingTag(sourceKey: SourceKey): LoggingTag = sourceKey.value

typealias LoggingEnabled = Boolean
