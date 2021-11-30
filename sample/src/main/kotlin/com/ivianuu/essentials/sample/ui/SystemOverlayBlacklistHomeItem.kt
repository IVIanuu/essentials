/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistKey
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayEnabled
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.flowOf

@Provide val systemOverlayBlacklistHomeItem = HomeItem("System overlay blacklist") {
  SystemOverlayBlacklistKey("Gestures")
}

@Provide val overlayEnabled = flowOf(SystemOverlayEnabled(false))
