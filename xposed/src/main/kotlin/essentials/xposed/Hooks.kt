/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.xposed

import injekt.*

fun interface Hooks {
  fun hook(@Provide config: XposedConfig)

  @Provide companion object {
    @Provide val defaultHooks get() = emptyList<Hooks>()
  }
}
