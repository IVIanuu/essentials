/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.onActive
import com.ivianuu.essentials.ui.common.registerActivityResultCallback
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@FunBinding
suspend fun startActivityForIntentResult(
    startActivityForResult: startActivityForResult<Intent, ActivityResult>,
    intent: @Assisted Intent
): ActivityResult = startActivityForResult(ActivityResultContracts.StartActivityForResult(), intent)

@FunBinding
suspend fun <I, O> startActivityForResult(
    startUi: startUi,
    navigator: Navigator,
    contract: @Assisted ActivityResultContract<I, O>,
    input: @Assisted I
): O {
    startUi()
    return suspendCancellableCoroutine { continuation ->
        var popped = false
        fun popIfNeeded() {
            if (!popped) {
                popped = true
                navigator.popTop()
            }
        }

        navigator.push(
            Route(opaque = true) {
                val launcher = registerActivityResultCallback(
                    contract,
                    ActivityResultCallback {
                        popIfNeeded()
                        continuation.resume(it)
                    }
                )

                onActive { launcher.launch(input) }
            }
        )
        continuation.invokeOnCancellation { popIfNeeded() }
    }
}
