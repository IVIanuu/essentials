package essentials.logging

import co.touchlab.kermit.*
import essentials.*
import injekt.*
import injekt.common.*

@JvmInline value class Logger(val kermitLogger: co.touchlab.kermit.Logger) {
  @Provide companion object {
    @Provide fun default(): @ScopedService<AppScope> Logger =
      Logger(co.touchlab.kermit.Logger(loggerConfigInit(platformLogWriter())))
  }
}

inline fun v(
  throwable: Throwable? = null,
  logger: Logger = inject,
  sourceKey: SourceKey = inject,
  message: () -> String
) = logger.kermitLogger.v(throwable, sourceKey.value, message)

inline fun d(
  throwable: Throwable? = null,
  logger: Logger = inject,
  sourceKey: SourceKey = inject,
  message: () -> String
) = logger.kermitLogger.d(throwable, sourceKey.value, message)

inline fun i(
  throwable: Throwable? = null,
  logger: Logger = inject,
  sourceKey: SourceKey = inject,
  message: () -> String
) = logger.kermitLogger.i(throwable, sourceKey.value, message)

inline fun w(
  throwable: Throwable? = null,
  logger: Logger = inject,
  sourceKey: SourceKey = inject,
  message: () -> String
) = logger.kermitLogger.w(throwable, sourceKey.value, message)

inline fun e(
  throwable: Throwable? = null,
  logger: Logger = inject,
  sourceKey: SourceKey = inject,
  message: () -> String
) = logger.kermitLogger.e(throwable, sourceKey.value, message)

inline fun a(
  throwable: Throwable? = null,
  logger: Logger = inject,
  sourceKey: SourceKey = inject,
  message: () -> String
) = logger.kermitLogger.a(throwable, sourceKey.value, message)
