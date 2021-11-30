/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.common.Elements

object AppScope

interface AppElementsOwner {
  val appElements: Elements<AppScope>
}
