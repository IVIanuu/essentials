/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.catchT
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.err
import com.ivianuu.essentials.ok
import com.ivianuu.essentials.onFailure
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.reflect.KClass

interface IntentKey : Key<Result<ActivityResult, ActivityNotFoundException>>

@Provide fun <@Spread T : KeyIntentFactory<K>, K : Any> intentKeyIntentFactory(
  intentFactory: T,
  keyClass: KClass<K>
): Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>> = (keyClass to intentFactory).cast()

fun interface KeyIntentFactory<T> : (T) -> Intent

fun interface IntentAppUiStarter : suspend () -> ComponentActivity

@Provide fun intentKeyHandler(
  appUiStarter: IntentAppUiStarter,
  context: MainContext,
  intentFactories: () -> Map<KClass<IntentKey>, KeyIntentFactory<IntentKey>>,
  scope: NamedCoroutineScope<AppScope>
) = KeyHandler<Result<ActivityResult, Throwable>> handler@ { key, onResult ->
  if (key !is IntentKey) return@handler false
  val intentFactory = intentFactories()[key::class as KClass<IntentKey>]
    ?: return@handler false
  val intent = intentFactory(key)
  launch {
    val activity = appUiStarter()
    withContext(context) {
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
