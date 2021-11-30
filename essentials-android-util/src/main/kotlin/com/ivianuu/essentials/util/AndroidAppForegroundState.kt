/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.compose.runtime.*
import com.ivianuu.essentials.app.*
import com.ivianuu.injekt.*

@Provide @Composable fun androidAppForegroundState(
  foregroundActivity: ForegroundActivity
) = if (foregroundActivity != null) AppForegroundState.FOREGROUND else AppForegroundState.BACKGROUND
