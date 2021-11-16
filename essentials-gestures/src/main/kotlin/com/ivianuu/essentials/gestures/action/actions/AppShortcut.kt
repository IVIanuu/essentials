package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.apps.shortcuts.AppShortcutPickerKey
import com.ivianuu.essentials.apps.shortcuts.AppShortcutRepository
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ACTION_DELIMITER
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.essentials.gestures.action.FloatingWindowActionsEnabled
import com.ivianuu.essentials.gestures.action.ui.FloatingWindowsPickerKey
import com.ivianuu.essentials.gestures.action.ui.LocalActionImageSizeModifier
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

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

    return {
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
