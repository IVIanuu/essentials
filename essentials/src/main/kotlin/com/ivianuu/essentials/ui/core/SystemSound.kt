package com.ivianuu.essentials.ui.core

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.staticAmbientOf

interface SystemSound {
    fun play(type: SystemSoundType)
}

enum class SystemSoundType {
    Click
}

val SystemSoundAmbient = staticAmbientOf<SystemSound>()

class AndroidSystemSound(private val view: View) : SystemSound {
    override fun play(type: SystemSoundType) {
        view.playSoundEffect(
            when (type) {
                SystemSoundType.Click -> SoundEffectConstants.CLICK
            }
        )
    }
}
