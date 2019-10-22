package com.ivianuu.essentials.ui.compose.common

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.effectOf
import androidx.compose.onActive

fun handleBack(activity: Activity, callback: () -> Unit) = effectOf<Unit> {
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