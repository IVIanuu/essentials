/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import android.content.*
import androidx.activity.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.*
import kotlin.reflect.*

interface IntentKey : Key<Result<ActivityResult, ActivityNotFoundException>>

@Provide fun <@Spread T : KeyIntentFactory<K>, K : Any> intentKeyIntentFactory(
  intentFactory: T,
  keyClass: KClass<K>
): Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>> = (keyClass to intentFactory) as Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>>

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
  scope.launch {
    val activity = appUiStarter()
    withContext(context) {
      val result =
        suspendCancellableCoroutine<Result<ActivityResult, Throwable>> { continuation ->
          val launcher = activity.activityResultRegistry.register(
            UUID.randomUUID().toString(),
            ActivityResultContracts.StartActivityForResult()
          ) {
            if (continuation.isActive) continuation.resume(Ok(it))
          }
          try {
            launcher.launch(intent)
          } catch (e: ActivityNotFoundException) {
            continuation.resume(Err(e))
          }
          continuation.invokeOnCancellation { launcher.unregister() }
        }
      onResult(result)
    }
  }
  true
}
