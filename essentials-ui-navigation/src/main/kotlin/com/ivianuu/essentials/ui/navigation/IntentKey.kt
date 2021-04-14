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

package com.ivianuu.essentials.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.reflect.KClass

interface IntentKey : Key<ActivityResult>

@Given
fun <@Given T : KeyIntentFactory<K>, K : Key<*>> keyIntentFactoryElement(
    @Given intentFactory: T,
    @Given keyClass: KClass<K>
): Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>> = (keyClass to intentFactory).cast()

typealias KeyIntentFactory<T> = (T) -> Intent

typealias IntentAppUiStarter = suspend () -> ComponentActivity

typealias IntentKeyHandler = (Key<*>, ((ActivityResult) -> Unit)?) -> Boolean

@Given
fun intentKeyHandler(
    @Given appContext: AppContext,
    @Given appUiStarter: IntentAppUiStarter,
    @Given dispatcher: MainDispatcher,
    @Given intentFactories: Map<KClass<IntentKey>, KeyIntentFactory<IntentKey>>,
    @Given scope: ScopeCoroutineScope<AppGivenScope>
): IntentKeyHandler = handler@ { key, onResult ->
    if (key !is IntentKey) return@handler false
    val intentFactory = intentFactories[key::class]
    if (intentFactory != null) {
        val intent = intentFactory(key)
        if (onResult != null) {
            scope.launch {
                val activity = appUiStarter()
                withContext(dispatcher) {
                    val result = suspendCancellableCoroutine<ActivityResult> { continuation ->
                        val launcher = activity.activityResultRegistry.register(
                            UUID.randomUUID().toString(),
                            ActivityResultContracts.StartActivityForResult()
                        ) { continuation.resume(it) }
                        launcher.launch(intent)
                        continuation.invokeOnCancellation { launcher.unregister() }
                    }
                    onResult(result)
                }
            }
        } else {
            appContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    intentFactory != null
}
