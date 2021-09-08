package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.d
import com.ivianuu.essentials.xposed.Hooks
import com.ivianuu.injekt.Provide

@Provide fun sampleHooks(logger: Logger): Hooks = {
  d { "hello from xposed $packageName" }
}
