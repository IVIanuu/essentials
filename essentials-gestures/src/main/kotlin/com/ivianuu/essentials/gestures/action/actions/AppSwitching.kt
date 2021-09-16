package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityOptionsCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.app.Eager
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.d
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
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

@Provide @Eager<AppScope> class AppSwitchManager(
  private val accessibilityEvents: Flow<AccessibilityEvent>,
  logger: Logger,
  private val packageManager: PackageManager,
  private val scope: NamedCoroutineScope<AppScope>
) {
  private val recentApps = mutableListOf<String>()
  private var currentIndex = 0
  private var isOnHomeScreen = false

  init {
    accessibilityEvents
      .filter {
        it.isFullScreen &&
            it.type == android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            it.className != "android.inputmethodservice.SoftInputWindow" &&
            (it.packageName !in getHomePackages(defaultOnly = false) ||
                it.packageName in getHomePackages(defaultOnly = true))
      }
      .mapNotNull { it.packageName }
      .distinctUntilChanged()
      .onEach { topApp ->
        val wasOnHomeScreen = isOnHomeScreen
        if (topApp in getHomePackages(defaultOnly = true)) {
          d { "moved to home screen" }
          isOnHomeScreen = true
          return@onEach
        } else isOnHomeScreen = false

        if (topApp !in recentApps) {
          recentApps += topApp
          currentIndex = recentApps.lastIndex
          d { "launched new app $topApp $recentApps $currentIndex" }
        } else if (wasOnHomeScreen) {
          val indexOfTopApp = recentApps.indexOf(topApp)
          recentApps.removeAt(indexOfTopApp)
          recentApps.add(topApp)
          currentIndex = recentApps.lastIndex
          d { "relaunched app from home screen $topApp $recentApps $currentIndex" }
        } else {
          currentIndex = recentApps.indexOf(topApp)
          d { "relaunched app from history $topApp $recentApps $currentIndex" }
        }
      }
      .launchIn(scope)
  }

  fun lastApp(): String? = if (isOnHomeScreen) recentApps.lastOrNull()
  else recentApps.getOrNull(currentIndex - 1)

  fun nextApp(): String? = recentApps.getOrNull(currentIndex + 1)

  private fun getHomePackages(defaultOnly: Boolean = true): List<String> =
    packageManager.queryIntentActivities(
      homeIntent(),
      if (defaultOnly) PackageManager.MATCH_DEFAULT_ONLY
      else PackageManager.MATCH_ALL
    ).map { it.activityInfo.packageName }

  private fun homeIntent() = Intent(Intent.ACTION_MAIN).apply {
    addCategory(Intent.CATEGORY_HOME)
  }
}

@Provide val appSwitchingAccessibilityConfig = flow {
  emit(
    AccessibilityConfig(
      eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
    )
  )
}