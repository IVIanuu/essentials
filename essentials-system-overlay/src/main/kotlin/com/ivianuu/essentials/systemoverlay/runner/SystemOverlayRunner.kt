package com.ivianuu.essentials.systemoverlay.runner

import android.view.WindowManager
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.injekt.Given

typealias SystemOverlayRunner = suspend () -> Unit

@Given
fun systemOverlayRunner(
    @Given states: Set<SystemOverlayAttachState<*>>
): SystemOverlayRunner = {
    runWithCleanup(
        block = {
            states.forEach {

            }
        },
        cleanup = {

        }
    )
}

typealias SystemOverlayWindowManager = WindowManager

typealias SystemOverlayWindowFlag = Int

typealias Full