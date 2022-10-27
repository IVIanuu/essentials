/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Serializable

@Serializable data class SystemOverlayBlacklistPrefs(
  val appBlacklist: Set<String> = emptySet(),
  val disableOnKeyboard: Boolean = false,
  val disableOnLockScreen: Boolean = true,
  val disableOnSecureScreens: Boolean = true
) {
  companion object {
    @Provide val prefModule = PrefModule { SystemOverlayBlacklistPrefs() }
  }
}
