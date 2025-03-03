package essentials.ui.navigation

import android.content.*
import androidx.activity.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import arrow.core.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.*
import kotlin.reflect.*

interface IntentScreen : Screen<Either<ActivityNotFoundException, ActivityResult>> {
  @Provide companion object {
    @Provide fun <@AddOn T : ScreenIntentFactory<K>, K : Any> intentFactoryBinding(
      intentFactory: T,
      keyClass: KClass<K>
    ): Pair<KClass<IntentScreen>, ScreenIntentFactory<IntentScreen>> =
      (keyClass to intentFactory).cast()
  }
}

fun interface ScreenIntentFactory<T> {
  suspend fun createIntent(screen: T): Intent
}

fun interface AppUiStarter {
  suspend fun startAppUi(): ComponentActivity
}

@Provide fun intentScreenInterceptor(
  appUiStarter: AppUiStarter,
  coroutineContexts: CoroutineContexts,
  intentFactories: () -> Map<KClass<IntentScreen>, ScreenIntentFactory<IntentScreen>>
) = ScreenInterceptor<Either<Throwable, ActivityResult>> handler@{ screen ->
  if (screen !is IntentScreen) return@handler null
  val intentFactory = intentFactories()[screen::class.cast()]
    ?: return@handler null
  val intent = intentFactory.createIntent(screen)
  return@handler {
    val activity = appUiStarter.startAppUi()
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
