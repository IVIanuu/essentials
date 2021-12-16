/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide fun androidAppForegroundState(
  foregroundActivity: Flow<ForegroundActivity>
): Flow<AppForegroundState> =
  foregroundActivity.map {
    if (it != null) AppForegroundState.FOREGROUND else AppForegroundState.BACKGROUND
  }

@Provide fun androidAppForegroundScopeHandler(
  foregroundElementsFactory: (Scope<AppForegroundScope>) -> Elements<AppForegroundScope>,
  foregroundState: Flow<AppForegroundState>
) = ScopeWorker<UiScope> {
  foregroundState.collectLatest { state ->
    if (state == AppForegroundState.FOREGROUND) {
      bracket(
        acquire = {
          val scope = Scope<AppForegroundScope>()
          foregroundElementsFactory(scope)
          scope
        },
        use = { awaitCancellation() },
        release = { scope, _ -> scope.dispose() }
      )
    }
  }
}
