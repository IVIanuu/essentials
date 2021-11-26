package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.xposed.Hooks
import com.ivianuu.injekt.Provide

@Provide fun sampleHooks(L: Logger) = Hooks {
  log { "hello from xposed ${it.packageName}" }
}
