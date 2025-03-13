package essentials.ui.navigation

import android.content.*
import androidx.activity.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import arrow.core.*
import essentials.*
import essentials.coroutines.*
import essentials.ui.UiScope
import injekt.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.*
import kotlin.reflect.*

interface IntentScreen : Screen<Either<ActivityNotFoundException, ActivityResult>> {
  @Provide companion object {
    @Provide fun <@AddOn T : ScreenIntent<S>, S : IntentScreen> intentFactoryBinding(
      intentFactory: (S) -> ScreenIntent<S>,
      key: KClass<S>
    ): Pair<KClass<IntentScreen>, (IntentScreen) -> Intent> =
      (key to intentFactory).cast()
  }
}

@Tag typealias ScreenIntent<T> = Intent

fun interface UiLauncher {
  suspend fun start(): Scope<UiScope>
}

@Provide fun interceptIntentScreen(
  screen: Screen<*>,
  uiLauncher: UiLauncher,
  coroutineContexts: CoroutineContexts,
  intentFactories: () -> Map<KClass<IntentScreen>, (IntentScreen) -> Intent>
): ScreenInterceptorResult<Either<Throwable, ActivityResult>> {
  if (screen !is IntentScreen) return null
  val intentFactory = intentFactories()[screen::class]
    ?: return null
  val intent = intentFactory(screen)
  return {
    val activity = uiLauncher.start()
      .service<ComponentActivity>()
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
