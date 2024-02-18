package com.ivianuu.essentials.ui.navigation

import android.content.*
import androidx.activity.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import arrow.core.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.*
import kotlin.reflect.*

interface IntentScreen : Screen<Either<ActivityNotFoundException, ActivityResult>> {
  @Provide companion object {
    @Provide fun <@Spread T : ScreenIntentFactory<K>, K : Any> intentFactoryBinding(
      intentFactory: T,
      keyClass: KClass<K>
    ): Pair<KClass<IntentScreen>, ScreenIntentFactory<IntentScreen>> =
      (keyClass to intentFactory).cast()
  }
}

fun interface ScreenIntentFactory<T> {
  suspend operator fun invoke(screen: T): Intent
}

fun interface AppUiStarter {
  suspend operator fun invoke(): ComponentActivity
}

@Provide fun intentScreenInterceptor(
  appUiStarter: AppUiStarter,
  coroutineContexts: CoroutineContexts,
  intentFactories: () -> Map<KClass<IntentScreen>, ScreenIntentFactory<IntentScreen>>
) = ScreenInterceptor<Either<Throwable, ActivityResult>> handler@{ screen ->
  if (screen !is IntentScreen) return@handler null
  val intentFactory = intentFactories()[screen::class.cast()]
    ?: return@handler null
  val intent = intentFactory(screen)
  return@handler {
    val activity = appUiStarter()
    withContext(coroutineContexts.main) {
      suspendCancellableCoroutine<Either<Throwable, ActivityResult>> { continuation ->
        val launcher = activity.activityResultRegistry.register(
          UUID.randomUUID().toString(),
          ActivityResultContracts.StartActivityForResult()
        ) {
          continuation.resume(it.right())
        }
        try {
          launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
          continuation.resume(e.left())
        }
        continuation.invokeOnCancellation { launcher.unregister() }
      }
    }
  }
}
