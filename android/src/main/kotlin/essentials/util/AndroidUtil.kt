package essentials.util

import android.annotation.SuppressLint
import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.res.Configuration
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import arrow.fx.coroutines.bracketCase
import com.jakewharton.processphoenix.ProcessPhoenix
import essentials.AppScope
import essentials.Scope
import essentials.appConfig
import essentials.appContext
import essentials.coroutines.coroutineContexts
import essentials.coroutines.coroutineScope
import essentials.logging.d
import essentials.packageManager
import essentials.scopeOf
import essentials.systemService
import essentials.ui.UiScope
import essentials.ui.navigation.UiLauncher
import injekt.Provide
import injekt.Tag
import injekt.inject
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration

suspend fun launchUi(scope: Scope<AppScope> = inject): Scope<UiScope> =
  withContext(coroutineContexts().main) {
    val intent = packageManager()
      .getLaunchIntentForPackage(appConfig().packageName)!!
    appContext().startActivity(
      intent.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    )

    scope.scopeOf<UiScope>().first()
  }

@Provide fun androidUiLauncher(scope: Scope<AppScope> = inject) = UiLauncher { launchUi() }

@Provide @Composable fun configuration(
  scope: Scope<*> = inject
): Configuration =
  produceState(appContext().resources.configuration) {
    val callbacks = object : ComponentCallbacks2 {
      override fun onConfigurationChanged(newConfig: Configuration) {
        value = newConfig
      }

      override fun onLowMemory() {
      }

      override fun onTrimMemory(level: Int) {
      }
    }
    appContext().registerComponentCallbacks(callbacks)
    awaitDispose { appContext().unregisterComponentCallbacks(callbacks) }
  }.value

@Tag typealias IsPowerSaveMode = Boolean

@Provide @Composable fun isPowerSaveMode(scope: Scope<*> = inject): IsPowerSaveMode =
  broadcastState(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
    systemService<PowerManager>().isPowerSaveMode
  }

suspend fun restartProcess(scope: Scope<*> = inject) {
  d { "restart process" }
  ProcessPhoenix.triggerRebirth(appContext())
}

fun showToast(message: String, scope: Scope<*> = inject) {
  coroutineScope().launch(coroutineContexts().main) {
    Toast.makeText(
      appContext(),
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}

@SuppressLint("MissingPermission")
suspend fun vibrate(duration: Duration, amplitude: Float, scope: Scope<*> = inject) {
  systemService<Vibrator>().vibrate(
    VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
  )
}

@Composable fun WakeLock(id: String, scope: Scope<*> = inject) {
  LaunchedEffect(id) {
    withContext(coroutineContexts().io) {
      bracketCase(
        acquire = { systemService<PowerManager>().newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id) },
        use = {
          d { "$id acquire wake lock" }
          it.acquire()
          awaitCancellation()
        },
        release = { wakeLock, _ ->
          d { "$id release wake lock" }
          wakeLock.release()
        }
      )
    }
  }
}
