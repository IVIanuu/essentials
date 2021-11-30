/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import android.app.Application
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope

abstract class EsApp : Application(), AppElementsOwner {
  @Provide private val scope = Scope<AppScope>()

  override lateinit var appElements: Elements<AppScope>
  override fun onCreate() {
    appElements = buildAppElements()
    super.onCreate()
  }

  protected abstract fun buildAppElements(@Inject scope: Scope<AppScope>): Elements<AppScope>
}
