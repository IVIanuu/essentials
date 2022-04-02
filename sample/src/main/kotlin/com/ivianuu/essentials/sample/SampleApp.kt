/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.EsApp
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Providers
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.inject

class SampleApp : EsApp() {
  override fun buildAppElements(@Inject scope: Scope<AppScope>): Elements<AppScope> =
    @Providers(
      ".**",
      "com.ivianuu.essentials.logging.AndroidLogger"
    ) inject()
}
