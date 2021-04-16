package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.systemoverlay.blacklist.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Given
fun systemOverlayBlacklistHomeItem(
    @Given gesturesState: Flow<SystemOverlayBlacklistState>
) = HomeItem("System overlay blacklist") {
    SystemOverlayBlacklistKey("Gestures")
}

@Given
val overlayEnabled = flowOf<SystemOverlayEnabled>(false)