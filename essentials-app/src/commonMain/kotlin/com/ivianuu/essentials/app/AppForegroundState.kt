/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import kotlinx.coroutines.flow.Flow

enum class AppForegroundState {
  FOREGROUND, BACKGROUND;

  @JvmInline value class Provider(val appForegroundState: Flow<AppForegroundState>)
}

object AppForegroundScope
