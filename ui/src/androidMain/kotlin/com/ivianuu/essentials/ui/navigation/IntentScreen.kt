package com.ivianuu.essentials.ui.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.reflect.KClass

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
          if (continuation.isActive) continuation.resume(it.right())
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
