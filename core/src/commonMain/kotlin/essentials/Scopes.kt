/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import injekt.*

data object AppScope

interface AppScopeOwner {
  val appScope: Scope<AppScope>
}

@Provide data object AppVisibleScope : ChildScopeMarker<AppVisibleScope, UiScope>

@Provide data object UiScope : ChildScopeMarker<UiScope, AppScope>
