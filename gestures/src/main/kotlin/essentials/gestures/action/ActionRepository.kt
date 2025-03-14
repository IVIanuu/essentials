/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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

@Provide @Service<AppScope> data class ActionDependencies(
  val actions: Map<String, () -> Action<*>>,
  val actionFactories: List<() -> ActionFactory>,
  val actionsExecutors: Map<String, suspend () -> ActionExecutorResult<*>>,
  val actionSettings: Map<String, () -> ActionSettingsScreen<ActionId>>,
  val actionPickerDelegates: List<() -> ActionPickerDelegate>
)

fun actionDependencies(scope: Scope<*> = inject): ActionDependencies = service()

suspend fun getAllActions(scope: Scope<*> = inject) = withContext(coroutineContexts().computation) {
  actionDependencies().actions.values.map { it() }
}

suspend fun String.toAction(scope: Scope<*> = inject) =
  withContext(coroutineContexts().computation) {
    catch {
      actionDependencies().actions[this@toAction]
        ?.invoke()
        ?: actionDependencies().actionFactories
          .fastMap { it() }
          .firstNotNullOfOrNull { it.createAction(this@toAction) }
    }
      .printErrors()
      .getOrNull()
      ?: Action(
        id = "error",
        title = RECONFIGURE_ACTION_MESSAGE,
        icon = { Icon(Icons.Default.Error, null) }
      )
  }

suspend fun String.getActionSettingsKey(scope: Scope<*> = inject) =
  withContext(coroutineContexts().computation) {
    actionDependencies().actionSettings[this@getActionSettingsKey]?.invoke()
  }

suspend fun getActionPickerDelegates(scope: Scope<*> = inject) =
  withContext(coroutineContexts().computation) {
    actionDependencies().actionPickerDelegates.fastMap { it() }
  }

suspend fun String.executeAction(scope: Scope<AppScope> = inject) =
  withContext(coroutineContexts().computation) {
    catch {
      d { "execute ${this@executeAction}" }
      val action = toAction()

      // check permissions
      if (!action.permissions.permissionState().first()) {
        d { "didn't had permissions for ${this@executeAction} ${action.permissions}" }
        unlockScreen()
        action.permissions.ensure()
        return@catch false
      }

      if (action.turnScreenOn && !turnScreenOn()) {
        d { "couldn't turn screen on for ${this@executeAction}" }
        return@catch false
      }

      // unlock screen
      if (action.unlockScreen && !unlockScreen()) {
        d { "couldn't unlock screen for ${this@executeAction}" }
        return@catch false
      }

      // close system dialogs
      if (action.closeSystemDialogs &&
        (appConfig().sdk < 31 ||
            listOf(ActionAccessibilityPermission::class).permissionState().first()))
        closeSystemDialogs()

      d { "fire ${this@executeAction}" }

      // fire
      catch {
        actionDependencies().actionsExecutors[this@executeAction]
          ?.invoke()
          ?: actionDependencies().actionFactories
            .fastMap { it() }
            .firstNotNullOfOrNull { it.execute(this@executeAction) }
      }.getOrNull()
        ?: showToast(RECONFIGURE_ACTION_MESSAGE)
      return@catch true
    }.onFailure {
      it.printStackTrace()
      showToast("Failed to execute action ${this@executeAction}!")
    }.getOrElse { false }
  }

private const val RECONFIGURE_ACTION_MESSAGE = "Error please reconfigure this action"
