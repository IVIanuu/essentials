/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import android.app.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

abstract class EsApp : Application(), AppElementsOwner {
  @Provide private val scope = Scope<AppScope>()

  override lateinit var appElements: Elements<AppScope>
  override fun onCreate() {
    appElements = buildAppElements()
    super.onCreate()
  }

  protected abstract fun buildAppElements(@Inject scope: Scope<AppScope>): Elements<AppScope>
}
