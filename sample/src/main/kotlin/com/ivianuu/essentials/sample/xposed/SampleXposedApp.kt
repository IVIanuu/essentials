package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.logging.XposedLogTag
import com.ivianuu.essentials.xposed.EsXposedApp
import com.ivianuu.essentials.xposed.ModulePackageName
import com.ivianuu.essentials.xposed.XposedAppComponent
import com.ivianuu.essentials.xposed.XposedContext
import com.ivianuu.essentials.xposed.createXposedAppComponent
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Providers

class SampleXposedApp : EsXposedApp() {
  @Providers(".**")
  override fun buildAppComponent(@Inject context: XposedContext): XposedAppComponent {
    @Provide val logTag = XposedLogTag("EssentialsSample")
    @Provide val modulePackageName = ModulePackageName("com.ivianuu.essentials.sample")
    return createXposedAppComponent()
  }
}
