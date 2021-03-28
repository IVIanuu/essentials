package com.ivianuu.essentials.systemoverlay.blacklist

import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.injekt.Given
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SystemOverlayBlacklistPrefs(
    @Json(name = "app_blacklist") val appBlacklist: Set<String> = emptySet(),
    @Json(name = "disable_on_keyboard") val disableOnKeyboard: Boolean = false,
    @Json(name = "disable_on_lock_screen") val disableOnLockScreen: Boolean = true,
    @Json(name = "disable_on_secure_screens") val disableOnSecureScreens: Boolean = true
)

@Given
val systemOverlayBlacklistPrefsModule =
    PrefModule<SystemOverlayBlacklistPrefs>("system_overlay_blacklist_prefs")
