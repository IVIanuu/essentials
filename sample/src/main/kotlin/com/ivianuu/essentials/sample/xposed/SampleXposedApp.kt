package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.logging.XposedLogTag
import com.ivianuu.essentials.xposed.EsXposedApp
import com.ivianuu.essentials.xposed.ModulePackageName
import com.ivianuu.essentials.xposed.XposedContext
import com.ivianuu.essentials.xposed.createXposedAppComponent
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Providers
import com.ivianuu.injekt.common.AppComponent

@Providers(
  "com.ivianuu.essentials.logging.XposedLogger",
  "com.ivianuu.essentials.xposed.*"
)
class SampleXposedApp : EsXposedApp() {
  override fun buildAppComponent(@Inject context: XposedContext): AppComponent {
    @Provide val logTag = XposedLogTag("EssentialsSample")
    @Provide val modulePackageName = ModulePackageName("com.ivianuu.essentials.sample")
    return createXposedAppComponent()
  }
}
