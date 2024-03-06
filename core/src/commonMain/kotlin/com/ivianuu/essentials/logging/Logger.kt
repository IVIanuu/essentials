package com.ivianuu.essentials.logging

import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scoped
import com.ivianuu.injekt.*
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.*

@Provide @JvmInline value class Logger(val kermitLogger: KermitLogger) {
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
    @Provide fun kermitLogger(): @Scoped<AppScope> KermitLogger =
      KermitLogger(loggerConfigInit(platformLogWriter()))
  }
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class KermitLoggerTag

typealias KermitLogger = @KermitLoggerTag co.touchlab.kermit.Logger
