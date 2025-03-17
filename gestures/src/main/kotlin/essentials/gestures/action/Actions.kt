/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.coroutines.*
import essentials.gestures.action.actions.*
import essentials.logging.*
import essentials.permission.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Stable @Provide class Actions(
  private val actions: Map<String, () -> Action<*>>,
  private val actionFactories: List<() -> ActionFactory>,
  private val actionSettings: Map<String, () -> ActionSettingsScreen<ActionId>>,
  private val actionPickerDelegates: List<() -> ActionPickerDelegate>,
  private val coroutineContexts: CoroutineContexts
) {
  suspend fun getAll() = withContext(coroutineContexts.computation) {
    actions.values.map { it() }
  }

  suspend fun get(id: String) = withContext(coroutineContexts.computation) {
    catch {
      actions[id]
        ?.invoke()
        ?: actionFactories
          .fastMap { it() }
          .firstNotNullOfOrNull { it.createAction(id) }
    }
      .printErrors()
      .getOrNull()
      ?: Action(
        id = "error",
        title = RECONFIGURE_ACTION_MESSAGE,
        icon = { Icon(Icons.Default.Error, null) }
      )
  }

  suspend fun getSettingsKey(id: String) =
    withContext(coroutineContexts.computation) { actionSettings[id]?.invoke() }

  suspend fun getPickerDelegates() =
    withContext(coroutineContexts.computation) { actionPickerDelegates.fastMap { it() } }
}

@Tag typealias executeActionResult = Boolean
typealias executeAction = suspend (String) -> executeActionResult

@Provide suspend fun executeAction(
  id: String,
  actionIds: List<ActionId>,
  actionsExecutors: Map<String, suspend (ActionId) -> ActionExecutorResult<*>>,
  actionFactories: List<() -> ActionFactory>,
  actions: Actions,
  appConfig: AppConfig,
  closeSystemDialogs: closeSystemDialogs,
  coroutineContexts: CoroutineContexts,
  logger: Logger = inject,
  permissions: Permissions,
  showToast: showToast,
  turnScreenOn: turnScreenOn,
  unlockScreen: unlockScreen
): executeActionResult = withContext(coroutineContexts.computation) {
  catch {
    d { "execute $id" }
    val action = actions.get(id)

    // check permissions
    if (!permissions.permissionState(action.permissions).first()) {
      d { "didn't had permissions for $id ${action.permissions}" }
      unlockScreen()
      permissions.ensurePermissions(action.permissions)
      return@catch false
    }

    if (action.turnScreenOn && !turnScreenOn()) {
      d { "couldn't turn screen on for $id" }
      return@catch false
    }

    // unlock screen
    if (action.unlockScreen && !unlockScreen()) {
      d { "couldn't unlock screen for $id" }
      return@catch false
    }

    // close system dialogs
    if (action.closeSystemDialogs &&
      (appConfig.sdk < 31 ||
          permissions.permissionState(listOf(ActionAccessibilityPermission::class)).first()))
      closeSystemDialogs()

    d { "fire $id" }

    // fire
    catch {
      actionsExecutors[id]
        ?.invoke(actionIds.first { it.value == id })
        ?: actionFactories
          .fastMap { it() }
          .firstNotNullOfOrNull { it.execute(id) }
    }.getOrNull()
      ?: showToast(RECONFIGURE_ACTION_MESSAGE)
    return@catch true
  }.onFailure {
    it.printStackTrace()
    showToast("Failed to execute action $id!")
  }.getOrElse { false }
}

private const val RECONFIGURE_ACTION_MESSAGE = "Error please reconfigure this action"
