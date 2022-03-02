/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.*
import android.content.pm.*
import androidx.core.app.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.flow.*

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
  accessibilityEvents: Flow<AccessibilityEvent>,
  private val packageManager: PackageManager,
  scope: NamedCoroutineScope<AppScope>,
  private val L: Logger
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
          log { "moved to home screen" }
          isOnHomeScreen = true
          return@onEach
        } else isOnHomeScreen = false

        if (topApp !in recentApps) {
          recentApps += topApp
          currentIndex = recentApps.lastIndex
          log { "launched new app $topApp $recentApps $currentIndex" }
        } else if (wasOnHomeScreen) {
          val indexOfTopApp = recentApps.indexOf(topApp)
          recentApps.removeAt(indexOfTopApp)
          recentApps.add(topApp)
          currentIndex = recentApps.lastIndex
          log { "relaunched app from home screen $topApp $recentApps $currentIndex" }
        } else {
          currentIndex = recentApps.indexOf(topApp)
          log { "relaunched app from history $topApp $recentApps $currentIndex" }
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

@Provide val appSwitchingAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
