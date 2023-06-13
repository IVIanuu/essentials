/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerScreen
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey

@Immutable data class Action<I : ActionId>(
  val id: String,
  val title: String,
  val permissions: List<TypeKey<Permission>> = emptyList(),
  val unlockScreen: Boolean = false,
  val closeSystemDialogs: Boolean = false,
  val turnScreenOn: Boolean = false,
  val enabled: Boolean = true,
  val icon: ActionIcon
) {
  constructor(
    id: I,
    title: String,
    permissions: List<TypeKey<Permission>> = emptyList(),
    unlockScreen: Boolean = false,
    closeSystemDialogs: Boolean = false,
    turnScreenOn: Boolean = false,
    enabled: Boolean = true,
    icon: ActionIcon
  ) : this(id.value, title, permissions, unlockScreen, closeSystemDialogs, turnScreenOn, enabled, icon)
}

fun interface ActionIcon {
  @Composable operator fun invoke()
}

abstract class ActionId(val value: String)

@Provide fun <@Spread T : Action<I>, I : ActionId> actionBinding(
  id: I,
  provider: () -> T,
): Pair<String, () -> Action<I>> = id.value to provider

fun interface ActionExecutor<I : ActionId> {
  suspend operator fun invoke()
}

@Provide fun <@Spread T : ActionExecutor<I>, I : ActionId> actionExecutorBinding(
  id: I,
  provider: () -> T
): Pair<String, () -> ActionExecutor<*>> = id.value to provider

interface ActionFactory {
  suspend fun handles(id: String): Boolean

  suspend fun createAction(id: String): Action<*>

  suspend fun createExecutor(id: String): ActionExecutor<*>
}

@Tag annotation class ActionSettingsKey<I : ActionId>

@Provide fun <@Spread T : @ActionSettingsKey<I> Screen<Unit>, I : ActionId> actionSettingsKeyBinding(
  id: I,
  provider: () -> T,
): Pair<String, () -> @ActionSettingsKey<ActionId> Screen<Unit>> = id.value to provider

interface ActionPickerDelegate {
  val baseId: String
  val title: String
  val settingsScreen: Screen<Unit>? get() = null
  val icon: @Composable () -> Unit

  suspend fun pickAction(): ActionPickerScreen.Result?
}

@JvmInline value class FloatingWindowActionsEnabled(val value: Boolean) {
  companion object {
    @Provide val default
      get() = FloatingWindowActionsEnabled(false)
  }
}

const val ACTION_DELIMITER = "=:="
