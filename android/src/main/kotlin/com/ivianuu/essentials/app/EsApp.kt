/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import android.app.*
import com.ivianuu.essentials.*

abstract class EsApp(appScope: EsApp.() -> Scope<AppScope>) : Application(), AppScopeOwner {
  override val appScope by lazy { appScope() }
}
