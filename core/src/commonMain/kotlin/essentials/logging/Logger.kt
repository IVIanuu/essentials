package essentials.logging

import androidx.compose.runtime.*
import co.touchlab.kermit.*
import essentials.*
import essentials.Scoped
import injekt.*
import injekt.common.*

@Stable @Provide @JvmInline value class Logger(val kermitLogger: co.touchlab.kermit.Logger) {
  inline fun v(throwable: Throwable? = null, sourceKey: SourceKey = inject, message: () -> String) =
    kermitLogger.v(throwable, sourceKey.value, message)

  inline fun d(throwable: Throwable? = null, sourceKey: SourceKey = inject, message: () -> String) =
    kermitLogger.d(throwable, sourceKey.value, message)

  inline fun i(throwable: Throwable? = null, sourceKey: SourceKey = inject, message: () -> String) =
    kermitLogger.i(throwable, sourceKey.value, message)

  inline fun w(throwable: Throwable? = null, sourceKey: SourceKey = inject, message: () -> String) =
    kermitLogger.w(throwable, sourceKey.value, message)

  inline fun e(throwable: Throwable? = null, sourceKey: SourceKey = inject, message: () -> String) =
    kermitLogger.e(throwable, sourceKey.value, message)

  inline fun a(throwable: Throwable? = null, sourceKey: SourceKey = inject, message: () -> String) =
    kermitLogger.a(throwable, sourceKey.value, message)

  @Provide companion object {
    @Provide fun kermitLogger(): @Scoped<AppScope> co.touchlab.kermit.Logger =
      co.touchlab.kermit.Logger(loggerConfigInit(platformLogWriter()))
  }
}

