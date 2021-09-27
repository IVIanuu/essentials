/*
 * Copyright 2021 Manuel Wrage
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

import com.ivianuu.essentials.logging.Logger.Priority.DEBUG
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey

interface Logger {
  val isEnabled: Boolean

  fun log(priority: Priority = DEBUG, @Inject tag: LoggingTag, message: String)

  enum class Priority {
    VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
  }
}

inline fun log(
  priority: Logger.Priority = DEBUG,
  @Inject tag: LoggingTag,
  @Inject logger: Logger,
  message: () -> String
) {
  if (logger.isEnabled) logger.log(priority, tag, message())
}

expect fun Throwable.asLog(): String

object NoopLogger : Logger {
  override val isEnabled: Boolean
    get() = false

  override fun log(priority: Logger.Priority, @Inject tag: LoggingTag, message: String) {
  }
}

@Provide class PrintingLogger(override val isEnabled: LoggingEnabled) : Logger {
  override fun log(priority: Logger.Priority, @Inject tag: LoggingTag, message: String) {
    println("[${priority.name}] $tag $message")
  }
}

typealias LoggingTag = String

@Provide inline fun loggingTag(sourceKey: SourceKey): LoggingTag = sourceKey.value

typealias LoggingEnabled = Boolean
