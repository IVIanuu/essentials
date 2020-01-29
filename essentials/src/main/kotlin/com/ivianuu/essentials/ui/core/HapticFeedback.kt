package com.ivianuu.essentials.ui.core

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.staticAmbientOf

interface HapticFeedback {
    fun vibrate(type: HapticFeedbackType)
}

enum class HapticFeedbackType {
    LongClick
}

val HapticFeedbackAmbient = staticAmbientOf<HapticFeedback>()

class AndroidHapticFeedback(private val view: View) : HapticFeedback {
    override fun vibrate(type: HapticFeedbackType) {
        view.performHapticFeedback(
            when (type) {
                HapticFeedbackType.LongClick -> HapticFeedbackConstants.LONG_PRESS
            }
        )
    }
}