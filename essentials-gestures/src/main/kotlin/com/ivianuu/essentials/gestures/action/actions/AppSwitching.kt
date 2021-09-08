package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityOptionsCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.d
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

fun switchToApp(
  packageName: String,
  enterAnimResId: Int,
  exitAnimResId: Int,
  @Inject actionIntentSender: ActionIntentSender,
  @Inject context: AppContext,
  @Inject packageManager: PackageManager
) {
  catch {
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return@catch
    intent.addFlags(
      Intent.FLAG_ACTIVITY_NEW_TASK or
          Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or
          Intent.FLAG_ACTIVITY_TASK_ON_HOME
    )
    actionIntentSender(
      intent, false, ActivityOptionsCompat.makeCustomAnimation(
        context,
        enterAnimResId,
        exitAnimResId
      ).toBundle()
    )
  }
}

@Provide @Scoped<AppScope> class AppSwitchManager(
  private val accessibilityEvents: Flow<AccessibilityEvent>,
  logger: Logger,
  private val packageManager: PackageManager,
  private val scope: InjektCoroutineScope<AppScope>
) {
  private val recentApps = mutableListOf<String>()
  private var currentIndex = 0

  init {
    accessibilityEvents
      .filter {
        it.isFullScreen &&
            it.type == android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
      }
      .filter {
        it.className != "android.inputmethodservice.SoftInputWindow" &&
            it.packageName != packageManager.getHomePackage()
      }
      .mapNotNull { it.packageName }
      .distinctUntilChanged()
      .onEach { currentApp ->
        if (currentApp !in recentApps) {
          recentApps += currentApp
          currentIndex = recentApps.lastIndex
          d { "new app launched $currentApp $recentApps $currentIndex" }
        } else {
          currentIndex = recentApps.indexOf(currentApp)
          d { "relaunched recent app $currentApp $recentApps $currentIndex" }
        }
      }
      .launchIn(scope)
  }

  fun nextApp(): String? = recentApps.getOrNull(currentIndex + 1)

  fun lastApp(): String? = recentApps.getOrNull(currentIndex - 1)

  private fun PackageManager.getHomePackage(): String {
    val intent = Intent(Intent.ACTION_MAIN).apply {
      addCategory(Intent.CATEGORY_HOME)
    }
    return resolveActivity(
      intent,
      PackageManager.MATCH_DEFAULT_ONLY
    )?.activityInfo?.packageName ?: ""
  }
}

@Provide fun appSwitchManagerStarter(manager: AppSwitchManager): ScopeWorker<AppScope> = {
}
