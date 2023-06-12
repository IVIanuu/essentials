/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.AppScopeOwner
import com.ivianuu.essentials.Scope

abstract class EsApp : Application(), AppScopeOwner {
  override val appScope by lazy { buildAppScope() }

  protected abstract fun buildAppScope(): Scope<AppScope>
}
