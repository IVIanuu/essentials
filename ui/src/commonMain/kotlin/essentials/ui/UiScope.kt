/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui

import essentials.*

data object UiScope

interface UiScopeOwner {
  val uiScope: Scope<UiScope>
}
