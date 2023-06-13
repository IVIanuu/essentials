/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistScreen
import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayEnabled
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.flowOf

@Provide val systemOverlayBlacklistHomeItem = HomeItem("System overlay blacklist") {
  SystemOverlayBlacklistScreen("Gestures")
}

@Provide val overlayEnabled = flowOf(SystemOverlayEnabled(false))
