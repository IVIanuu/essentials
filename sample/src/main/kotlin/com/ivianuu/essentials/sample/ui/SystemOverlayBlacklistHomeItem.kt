package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.systemoverlay.blacklist.SystemOverlayBlacklistKey
import com.ivianuu.injekt.Given

@Given
val systemOverlayBlacklistHomeItem = HomeItem("System overlay blacklist") {
    SystemOverlayBlacklistKey("Gestures")
}
