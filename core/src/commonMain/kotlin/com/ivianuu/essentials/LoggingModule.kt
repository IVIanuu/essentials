package com.ivianuu.essentials

import co.touchlab.kermit.*
import com.ivianuu.injekt.*

@Provide object LoggingModule {
  @Provide fun logger(): @Scoped<AppScope> Logger =
    Logger(loggerConfigInit(platformLogWriter()))
}
