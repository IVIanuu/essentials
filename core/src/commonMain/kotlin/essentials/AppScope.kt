/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

data object AppScope

interface AppScopeOwner {
  val appScope: Scope<AppScope>
}
