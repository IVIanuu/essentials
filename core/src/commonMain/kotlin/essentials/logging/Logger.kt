package essentials.logging

import androidx.compose.runtime.*
import co.touchlab.kermit.*
import essentials.*
import injekt.*
import injekt.common.SourceKey

inline fun v(
  throwable: Throwable? = null,
  sourceKey: SourceKey = inject,
  scope: Scope<*> = inject,
  message: () -> String
) = service<Logger>().v(throwable, sourceKey.value, message)

inline fun d(
  throwable: Throwable? = null,
  sourceKey: SourceKey = inject,
  scope: Scope<*> = inject,
  message: () -> String
) = service<Logger>().d(throwable, sourceKey.value, message)

inline fun i(
  throwable: Throwable? = null,
  sourceKey: SourceKey = inject,
  scope: Scope<*> = inject,
  message: () -> String
) = service<Logger>().i(throwable, sourceKey.value, message)

inline fun w(
  throwable: Throwable? = null,
  sourceKey: SourceKey = inject,
  scope: Scope<*> = inject,
  message: () -> String
) = service<Logger>().w(throwable, sourceKey.value, message)

inline fun e(
  throwable: Throwable? = null,
  sourceKey: SourceKey = inject,
  scope: Scope<*> = inject,
  message: () -> String
) = service<Logger>().e(throwable, sourceKey.value, message)

inline fun a(
  throwable: Throwable? = null,
  sourceKey: SourceKey = inject,
  scope: Scope<*> = inject,
  message: () -> String
) = service<Logger>().a(throwable, sourceKey.value, message)


@Provide object LoggerProviders {
  @Provide fun kermitLogger(): @ScopedService<AppScope> Logger =
    Logger(loggerConfigInit(platformLogWriter()))
}
