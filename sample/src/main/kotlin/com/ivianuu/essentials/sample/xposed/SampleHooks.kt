package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.logging.Log
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.xposed.Hooks
import com.ivianuu.injekt.Provide

@Provide @Log val sampleHooks: Hooks
  get() = {
    log { "hello from xposed $packageName" }
  }
