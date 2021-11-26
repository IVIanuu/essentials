/*
 * Copyright 2021 Manuel Wrage
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

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.catchT
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.err
import com.ivianuu.essentials.ok
import com.ivianuu.essentials.onFailure
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.coroutines.ComponentScope
import com.ivianuu.injekt.coroutines.MainDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.reflect.KClass

interface IntentKey : Key<Result<ActivityResult, ActivityNotFoundException>>

@Provide fun <@Spread T : KeyIntentFactory<K>, K : Any> keyIntentFactoryElement(
  intentFactory: T,
  keyClass: KClass<K>
): Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>> = (keyClass to intentFactory).cast()

fun interface KeyIntentFactory<T> : (T) -> Intent

fun interface IntentAppUiStarter : suspend () -> ComponentActivity

@Provide fun intentKeyHandler(
  appUiStarter: IntentAppUiStarter,
  dispatcher: MainDispatcher,
  intentFactories: () -> Map<KClass<IntentKey>, KeyIntentFactory<IntentKey>>,
  scope: ComponentScope<AppComponent>
) = KeyHandler<Result<ActivityResult, Throwable>> handler@ { key, onResult ->
  if (key !is IntentKey) return@handler false
  val intentFactory = intentFactories()[key::class as KClass<IntentKey>]
    ?: return@handler false
  val intent = intentFactory(key)
  launch {
    val activity = appUiStarter()
    withContext(dispatcher) {
      val result =
        suspendCancellableCoroutine<Result<ActivityResult, Throwable>> { continuation ->
          val launcher = activity.activityResultRegistry.register(
            UUID.randomUUID().toString(),
            ActivityResultContracts.StartActivityForResult()
          ) {
            if (continuation.isActive) continuation.resume(it.ok())
          }
          catchT<Unit, ActivityNotFoundException> { launcher.launch(intent) }
            .onFailure { continuation.resume(it.err()) }
          continuation.invokeOnCancellation { launcher.unregister() }
        }
      onResult(result)
    }
  }
  true
}
