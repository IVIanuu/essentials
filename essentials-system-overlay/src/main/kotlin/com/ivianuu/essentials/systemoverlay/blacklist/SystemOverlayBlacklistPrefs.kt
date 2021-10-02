/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.injekt.Provide
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
