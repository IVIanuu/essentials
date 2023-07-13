/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.app.EsApp
import com.ivianuu.essentials.contextWith
import com.ivianuu.essentials.logging.AndroidLogger

class SampleApp : EsApp() {
  override fun buildAppScope() = contextWith<_, Scope<AppScope>>(AndroidLogger::androidLogger)
}
