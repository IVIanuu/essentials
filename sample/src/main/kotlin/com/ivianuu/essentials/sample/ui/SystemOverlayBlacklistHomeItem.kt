/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.systemoverlay.blacklist.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide val systemOverlayBlacklistHomeItem = HomeItem("System overlay blacklist") {
  SystemOverlayBlacklistKey("Gestures")
}

@Provide val overlayEnabled = flowOf(SystemOverlayEnabled(false))
