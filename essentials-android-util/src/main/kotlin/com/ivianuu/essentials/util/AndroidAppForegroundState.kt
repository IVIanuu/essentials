/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import com.ivianuu.essentials.app.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide fun androidAppForegroundState(
  foregroundActivity: Flow<ForegroundActivity>
): Flow<AppForegroundState> =
  foregroundActivity.map {
    if (it != null) AppForegroundState.FOREGROUND else AppForegroundState.BACKGROUND
  }
