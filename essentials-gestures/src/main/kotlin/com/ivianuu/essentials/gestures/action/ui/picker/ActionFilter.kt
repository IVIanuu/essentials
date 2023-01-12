/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.ui.picker

import com.ivianuu.injekt.Provide

fun interface ActionFilter {
  operator fun invoke(actionId: String): Boolean

  companion object {
    @Provide val default = ActionFilter { true }
  }
}
