/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.xposed

import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.xposed.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

class SampleXposedApp : EsXposedApp() {
  @Providers(".**") override fun buildXposedElements(
    @Inject context: XposedContext,
    @Inject scope: Scope<XposedScope>
  ): Elements<XposedScope> {
    @Provide val logTag = XposedLogTag("EssentialsSample")
    @Provide val modulePackageName = ModulePackageName("com.ivianuu.essentials.sample")
    return inject()
  }
}
