/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui

import com.ivianuu.essentials.Scope

data object UiScope

interface UiScopeOwner {
  val uiScope: Scope<UiScope>
}
