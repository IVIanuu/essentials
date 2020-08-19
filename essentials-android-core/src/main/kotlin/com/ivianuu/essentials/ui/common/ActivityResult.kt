package com.ivianuu.essentials.ui.common

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember

@Composable
fun <I, O> registerActivityResultCallback(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    val key = currentComposer.currentCompoundKeyHash

    val registry = compositionActivity.activityResultRegistry

    val launcher = remember(contract, callback, registry) {
        registry.register(
            key.toString(),
            contract,
            callback
        )
    }

    key(launcher) {
        onDispose {
            launcher.unregister()
        }
    }

    return launcher
}
