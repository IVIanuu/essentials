package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.injekt.*
import kotlinx.serialization.*

@Serializable
data class SystemOverlayBlacklistPrefs(
    @SerialName("app_blacklist") val appBlacklist: Set<String> = emptySet(),
    @SerialName("disable_on_keyboard") val disableOnKeyboard: Boolean = false,
    @SerialName("disable_on_lock_screen") val disableOnLockScreen: Boolean = true,
    @SerialName("disable_on_secure_screens") val disableOnSecureScreens: Boolean = true
)

@Given
val systemOverlayBlacklistPrefsModule = PrefModule("system_overlay_blacklist_prefs") {
    SystemOverlayBlacklistPrefs()
}
