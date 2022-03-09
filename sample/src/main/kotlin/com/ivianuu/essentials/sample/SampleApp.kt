/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*

class SampleApp : EsApp() {
  override fun buildAppElements(scope: Scope<AppScope>): Elements<AppScope> =
    @Providers(
      ".**",
      "com.ivianuu.essentials.logging.AndroidLogger"
    ) inject()
}
