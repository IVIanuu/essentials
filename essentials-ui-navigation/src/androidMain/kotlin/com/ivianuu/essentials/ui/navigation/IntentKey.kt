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
import com.ivianuu.injekt.coroutines.MainContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.reflect.KClass

interface IntentKey : Key<Result<ActivityResult, ActivityNotFoundException>>

@Provide fun <@Spread T : KeyIntentFactory<K>, K : Any> intentKeyIntentFactory(
  intentFactory: T,
  keyClass: KClass<K>
): Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>> = (keyClass to intentFactory) as Pair<KClass<IntentKey>, KeyIntentFactory<IntentKey>>

fun interface KeyIntentFactory<T> {
  suspend operator fun invoke(key: T): Intent
}

fun interface IntentAppUiStarter {
  suspend fun startAppUi(): ComponentActivity
}

context(IntentAppUiStarter)
    @Provide fun intentKeyHandler(
  context: MainContext,
  intentFactories: () -> Map<KClass<IntentKey>, KeyIntentFactory<IntentKey>>
) = KeyInterceptor<Result<ActivityResult, Throwable>> handler@{ key ->
  if (key !is IntentKey) return@handler null
  val intentFactory = intentFactories()[key::class as KClass<IntentKey>]
    ?: return@handler null
  val intent = intentFactory(key)
  return@handler {
    val activity = startAppUi()
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
