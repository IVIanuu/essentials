package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.systemoverlay.blacklist.*
import com.ivianuu.injekt.*

@Given
val systemOverlayBlacklistHomeItem = HomeItem("System overlay blacklist") {
    SystemOverlayBlacklistKey("Gestures")
}
