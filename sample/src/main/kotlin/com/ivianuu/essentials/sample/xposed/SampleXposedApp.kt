package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.xposed.EsXposedApp
import com.ivianuu.essentials.xposed.XposedContext
import com.ivianuu.essentials.xposed.createXposedAppScope
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Providers
import com.ivianuu.injekt.scope.AppScope

@Providers(
  "com.ivianuu.essentials.xposed.*",
  "com.ivianuu.injekt.scope.*"
)
class SampleXposedApp : EsXposedApp() {
  override fun buildAppScope(@Inject context: XposedContext): AppScope =
    createXposedAppScope()
}
