/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

data class Action<I : ActionId>(
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

fun interface ActionIcon : @Composable () -> Unit

abstract class ActionId(val value: String)

@Provide fun <@Spread T : Action<I>, I : ActionId> actionPair(
  id: I,
  provider: () -> T,
): Pair<String, () -> Action<I>> = id.value to provider

fun interface ActionExecutor<I : ActionId> : suspend () -> Unit

@Provide fun <@Spread T : ActionExecutor<I>, I : ActionId> actionExecutorPair(
  id: I,
  provider: () -> T
): Pair<String, () -> ActionExecutor<*>> = id.value to provider

interface ActionFactory {
  suspend fun handles(id: String): Boolean

  suspend fun createAction(id: String): Action<*>

  suspend fun createExecutor(id: String): ActionExecutor<*>
}

@Tag annotation class ActionSettingsKey<I : ActionId>

@Provide fun <@Spread T : @ActionSettingsKey<I> Key<Unit>, I : ActionId> actionSettingsKeyPair(
  id: I,
  provider: () -> T,
): Pair<String, () -> @ActionSettingsKey<ActionId> Key<Unit>> = id.value to provider

interface ActionPickerDelegate {
  val baseId: String
  val title: String
  val settingsKey: Key<Unit>? get() = null

  @Composable fun Icon()

  suspend fun pickAction(): ActionPickerKey.Result?
}

@JvmInline value class FloatingWindowActionsEnabled(val value: Boolean) {
  companion object {
    @Provide fun floatingWindowActionsEnabled(
      systemBuildInfo: SystemBuildInfo
    ) = FloatingWindowActionsEnabled(systemBuildInfo.sdk >= 29)
  }
}

const val ACTION_DELIMITER = "=:="
