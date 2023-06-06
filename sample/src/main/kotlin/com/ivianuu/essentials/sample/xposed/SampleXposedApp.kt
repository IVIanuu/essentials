/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.logging.XposedLogTag
import com.ivianuu.essentials.xposed.EsXposedApp
import com.ivianuu.essentials.xposed.ModulePackageName
import com.ivianuu.essentials.xposed.XposedContext
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Providers
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.inject

class SampleXposedApp : EsXposedApp() {
  @Providers(".**") override fun buildXposedElements(
    @Inject ctx: XposedContext,
    @Inject scope: Scope<AppScope>
  ): Elements<AppScope> {
    @Provide val logTag = XposedLogTag("EssentialsSample")
    @Provide val modulePackageName = ModulePackageName("com.ivianuu.essentials.sample")
    return inject()
  }
}
