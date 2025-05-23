package essentials.ui.navigation

import android.content.*
import androidx.activity.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.*
import kotlin.reflect.*

interface IntentScreen : Screen<Result<ActivityResult, ActivityNotFoundException>> {
  @Provide companion object {
    @Provide fun <@AddOn T : ScreenIntent<S>, S : IntentScreen> intentFactoryBinding(
      intentFactory: (S) -> ScreenIntent<S>,
      key: KClass<S>
    ): Pair<KClass<IntentScreen>, (IntentScreen) -> Intent> =
      (key to intentFactory).cast()
  }
}

@Tag typealias ScreenIntent<T> = Intent

@Tag typealias launchUiResult = Scope<UiScope>
typealias launchUi = suspend () -> launchUiResult

@Provide fun interceptIntentScreen(
  screen: Screen<*>,
  launchUi: launchUi,
  coroutineContexts: CoroutineContexts,
  intentFactories: () -> Map<KClass<IntentScreen>, (IntentScreen) -> Intent>
): ScreenInterceptorResult<Result<ActivityResult, Throwable>> {
  if (screen !is IntentScreen) return null
  val intentFactory = intentFactories()[screen::class]
    ?: return null
  val intent = intentFactory(screen)
  return {
    val activity = launchUi().service<ComponentActivity>()
    withContext(coroutineContexts.main) {
      suspendCancellableCoroutine<Result<ActivityResult, Throwable>> { continuation ->
        val launcher = activity.activityResultRegistry.register(
          UUID.randomUUID().toString(),
          ActivityResultContracts.StartActivityForResult()
        ) {
          continuation.resume(it.ok())
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
