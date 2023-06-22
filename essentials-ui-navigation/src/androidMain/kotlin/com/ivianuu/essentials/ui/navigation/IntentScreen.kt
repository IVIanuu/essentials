/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.err
import com.ivianuu.essentials.ok
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.MainCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.reflect.KClass

interface IntentScreen : Screen<Result<ActivityResult, ActivityNotFoundException>>

@Provide fun <@Spread T : ScreenIntentFactory<K>, K : Any> intentKeyIntentFactory(
  intentFactory: T,
  keyClass: KClass<K>
): Pair<KClass<IntentScreen>, ScreenIntentFactory<IntentScreen>> =
  (keyClass to intentFactory) as Pair<KClass<IntentScreen>, ScreenIntentFactory<IntentScreen>>

fun interface ScreenIntentFactory<T> {
  suspend operator fun invoke(screen: T): Intent
}

fun interface IntentAppUiStarter {
  suspend operator fun invoke(): ComponentActivity
}

@Provide fun intentScreenInterceptor(
  appUiStarter: IntentAppUiStarter,
  context: MainCoroutineContext,
  intentFactories: () -> Map<KClass<IntentScreen>, ScreenIntentFactory<IntentScreen>>
) = ScreenInterceptor<Result<ActivityResult, Throwable>> handler@{ screen ->
  if (screen !is IntentScreen) return@handler null
  val intentFactory = intentFactories()[screen::class as KClass<IntentScreen>]
    ?: return@handler null
  val intent = intentFactory(screen)
  return@handler {
    val activity = appUiStarter()
    withContext(context) {
      suspendCancellableCoroutine<Result<ActivityResult, Throwable>> { continuation ->
        val launcher = activity.activityResultRegistry.register(
          UUID.randomUUID().toString(),
          ActivityResultContracts.StartActivityForResult()
        ) {
          if (continuation.isActive) continuation.resume(it.ok())
        }
        try {
          launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
          continuation.resume(e.err())
        }
        continuation.invokeOnCancellation { launcher.unregister() }
      }
    }
  }
}
