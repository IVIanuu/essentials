package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.android.prefs.PrefDataStoreModule
import com.ivianuu.injekt.Given
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemOverlayBlacklistPrefs(
    @SerialName("app_blacklist") val appBlacklist: Set<String> = emptySet(),
    @SerialName("disable_on_keyboard") val disableOnKeyboard: Boolean = false,
    @SerialName("disable_on_lock_screen") val disableOnLockScreen: Boolean = true,
    @SerialName("disable_on_secure_screens") val disableOnSecureScreens: Boolean = true
)

@Given
val systemOverlayBlacklistPrefsModule =
    PrefDataStoreModule<SystemOverlayBlacklistPrefs>("system_overlay_blacklist_prefs")
