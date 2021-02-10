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
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume

@GivenFun
suspend fun startActivityForIntentResult(
    intent: Intent,
    @Given startActivityForResult: startActivityForResult<Intent, ActivityResult>
): ActivityResult = startActivityForResult(ActivityResultContracts.StartActivityForResult(), intent)

@GivenFun
suspend fun <I, O> startActivityForResult(
    contract: ActivityResultContract<I, O>,
    input: I,
    @Given mainDispatcher: MainDispatcher,
    @Given openAppUi: openAppUi
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
