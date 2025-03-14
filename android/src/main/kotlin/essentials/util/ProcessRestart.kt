/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import com.jakewharton.processphoenix.*
import essentials.*
import essentials.logging.*
import injekt.*

suspend fun restartProcess(scope: Scope<*> = inject) {
  d { "restart process" }
  ProcessPhoenix.triggerRebirth(appContext())
}
