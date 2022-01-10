/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.injekt.*
import kotlinx.serialization.*

@Serializable data class SystemOverlayBlacklistPrefs(
  @SerialName("app_blacklist") val appBlacklist: Set<String> = emptySet(),
  @SerialName("disable_on_keyboard") val disableOnKeyboard: Boolean = false,
  @SerialName("disable_on_lock_screen") val disableOnLockScreen: Boolean = true,
  @SerialName("disable_on_secure_screens") val disableOnSecureScreens: Boolean = true
) {
  companion object {
    @Provide val prefModule = PrefModule { SystemOverlayBlacklistPrefs() }
  }
}
