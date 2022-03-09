/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.essentials.di.*

object AppScope

interface AppElementsOwner {
  val appElements: Elements<AppScope>
}
