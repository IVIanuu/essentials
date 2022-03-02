/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.shortcuts.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.ui.image.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3

@Provide class AppShortcutActionFactory(
  private val actionIntentSender: ActionIntentSender,
  private val appShortcutRepository: AppShortcutRepository,
  private val RP: ResourceProvider
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val (packageName, shortcutId) = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)
      .let { it[0] to it[1] }

    val appShortcut = appShortcutRepository.appShortcut(packageName, shortcutId).first()!!

    return Action<ActionId>(
      id = id,
      title = appShortcut.shortLabel,
      unlockScreen = true,
      closeSystemDialogs = true,
      enabled = true,
      permissions = listOf(typeKeyOf<ActionSystemOverlayPermission>()),
      icon = {
        Image(
          modifier = LocalActionImageSizeModifier.current,
          bitmap = appShortcut.icon.toBitmap().toImageBitmap()
        )
      }
    )
  }

  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val (packageName, shortcutId, isFloating) = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)

    val appShortcut = appShortcutRepository.appShortcut(packageName, shortcutId).first()!!

    return ActionExecutor<ActionId> {
      actionIntentSender(
        appShortcut.intent,
        isFloating.toBoolean(),
        null
      )
    }
  }
}

@Provide class AppShortcutActionPickerDelegate(
  private val floatingWindowActionsEnabled: FloatingWindowActionsEnabled,
  private val navigator: Navigator,
  private val RP: ResourceProvider
) : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID

  override val title: String
    get() = loadResource(R.string.es_action_app_shortcut)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_apps)
  }

  override suspend fun pickAction(): ActionPickerKey.Result? {
    val shortcut = navigator.push(AppShortcutPickerKey) ?: return null
    val isFloating = floatingWindowActionsEnabled.value &&
        navigator.push(FloatingWindowsPickerKey(shortcut.shortLabel)) ?: return null
    return ActionPickerKey.Result.Action("$BASE_ID${shortcut.packageName}" +
        "$ACTION_DELIMITER${shortcut.id}$ACTION_DELIMITER$isFloating")
  }
}

private const val BASE_ID = "app_shortcut$ACTION_DELIMITER"
