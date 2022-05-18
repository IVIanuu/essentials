/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ivianuu.essentials.EsResult
import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.analytics.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.coroutines.MainContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.reflect.KClass

interface IntentKey : Key<EsResult<ActivityResult, ActivityNotFoundException>>

@Provide fun <@Spread T : KeyIntentFactory<K>, K : Any> intentKeyIntentFactory(
  intentFactory: T,
  keyClass: KClass<K>
): Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>> = (keyClass to intentFactory) as Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>>

fun interface KeyIntentFactory<T> : (T) -> Intent

fun interface IntentAppUiStarter : suspend () -> ComponentActivity

@Provide fun intentKeyHandler(
  analytics: Analytics,
  appUiStarter: IntentAppUiStarter,
  context: MainContext,
  intentFactories: () -> Map<KClass<IntentKey>, KeyIntentFactory<IntentKey>>
) = KeyHandler<EsResult<ActivityResult, Throwable>> handler@ { key ->
  if (key !is IntentKey) return@handler null
  val intentFactory = intentFactories()[key::class as KClass<IntentKey>]
    ?: return@handler null
  val intent = intentFactory(key)
  return@handler {
    if (intent.data?.toString()?.startsWith("https") == true) {
      analytics.log("url_launched") {
        put("url", intent.data.toString())
      }
    }

    val activity = appUiStarter()
    withContext(context) {
      suspendCancellableCoroutine<EsResult<ActivityResult, Throwable>> { continuation ->
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
    }
  }
}
