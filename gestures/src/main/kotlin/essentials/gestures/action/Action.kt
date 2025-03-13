/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action

import androidx.compose.runtime.*
import essentials.gestures.action.ui.*
import essentials.permission.*
import essentials.ui.navigation.*
import injekt.*
import kotlin.reflect.*

@Immutable data class Action<I : ActionId>(
  val id: String,
  val title: String,
  val permissions: List<KClass<out Permission>> = emptyList(),
  val unlockScreen: Boolean = false,
  val closeSystemDialogs: Boolean = false,
  val turnScreenOn: Boolean = false,
  val enabled: Boolean = true,
  val icon: @Composable () -> Unit
) {
  constructor(
    id: I,
    title: String,
    permissions: List<KClass<out Permission>> = emptyList(),
    unlockScreen: Boolean = false,
    closeSystemDialogs: Boolean = false,
    turnScreenOn: Boolean = false,
    enabled: Boolean = true,
    icon: @Composable () -> Unit
  ) : this(id.value, title, permissions, unlockScreen, closeSystemDialogs, turnScreenOn, enabled, icon)
}

abstract class ActionId(val value: String)

@Tag typealias ActionExecutorResult<I> = Unit

interface ActionFactory {
  suspend fun createAction(id: String): Action<*>?

  suspend fun execute(id: String): ActionExecutorResult<*>?
}

@Tag typealias ActionSettingsScreen<I> = Screen<Unit>

interface ActionPickerDelegate {
  val baseId: String
  val title: String
  val settingsScreen: Screen<Unit>? get() = null
  val icon: @Composable () -> Unit

  suspend fun pickAction(navigator: Navigator): ActionPickerScreen.Result?
}

@Provide object ActionProviders {
  @Provide fun <@AddOn T : Action<I>, I : ActionId> actionBinding(
    id: I,
    provider: () -> T,
  ): Pair<String, () -> Action<*>> = id.value to provider

  @Provide fun <@AddOn T : ActionFactory> actionFactoryBinding(
    provider: () -> T
  ): () -> ActionFactory = provider

  @Provide fun <@AddOn T : ActionExecutorResult<I>, I : ActionId> actionExecutorBinding(
    id: I,
    provider: suspend () -> T
  ): Pair<String, suspend () -> ActionExecutorResult<*>> = id.value to provider

  @Provide fun <@AddOn T : ActionPickerDelegate> actionPickerDelegateBinding(
    provider: () -> T
  ): () -> ActionPickerDelegate = provider

  @Provide fun <@AddOn T : ActionSettingsScreen<I>, I : ActionId> actionSettingsKeyBinding(
    id: I,
    provider: () -> T,
  ): Pair<String, () -> ActionSettingsScreen<ActionId>> = id.value to provider
}

const val ACTION_DELIMITER = "=:="
