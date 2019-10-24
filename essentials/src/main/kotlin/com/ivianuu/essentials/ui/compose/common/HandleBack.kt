package com.ivianuu.essentials.ui.compose.common

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.onActive
import androidx.compose.unaryPlus
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient

fun handleBack(
    activity: Activity = +ambient(ActivityAmbient),
    callback: () -> Unit
) = effectOf<Unit> {
    +onActive {
        val backPressedDispatcher =
            (activity as OnBackPressedDispatcherOwner).onBackPressedDispatcher

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback()
            }
        }

        backPressedDispatcher.addCallback(onBackPressedCallback)

        onDispose { onBackPressedCallback.remove() }
    }
}