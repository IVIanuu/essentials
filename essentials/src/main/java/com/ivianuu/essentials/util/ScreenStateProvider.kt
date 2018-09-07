package com.ivianuu.essentials.util

import android.app.KeyguardManager
import android.os.PowerManager
import javax.inject.Inject

/**
 * Screen state provider
 */
class ScreenStateProvider @Inject constructor(
    private val keyguardManager: KeyguardManager,
    private val powerManager: PowerManager
) {

    val isScreenOn get() = powerManager.isScreenOn

    val isScreenOff get() = !isScreenOn
}