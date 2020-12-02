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
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.onActive
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.ui.common.registerActivityResultCallback
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume

@FunBinding
suspend fun startActivityForIntentResult(
    startActivityForResult: startActivityForResult<Intent, ActivityResult>,
    @FunApi intent: Intent
): ActivityResult = startActivityForResult(ActivityResultContracts.StartActivityForResult(), intent)

@FunBinding
suspend fun <I, O> startActivityForResult(
    mainDispatcher: MainDispatcher,
    openAppUi: openAppUi,
    @FunApi contract: ActivityResultContract<I, O>,
    @FunApi input: I,
): O {
    val activity = openAppUi()
    return withContext(mainDispatcher) {
        suspendCancellableCoroutine { continuation ->
            val launcher = activity.activityResultRegistry.register(
                UUID.randomUUID().toString(),
                activity,
                contract,
                { continuation.resume(it) }
            )
            launcher.launch(input)
            continuation.invokeOnCancellation { launcher.unregister() }
        }
    }
}
