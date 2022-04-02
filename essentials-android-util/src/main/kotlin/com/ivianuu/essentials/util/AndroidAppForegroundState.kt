/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import com.ivianuu.essentials.app.AppForegroundScope
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

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
