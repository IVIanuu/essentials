/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
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
  val icon: ActionIcon
) {
  constructor(
    id: I,
    title: String,
    permissions: List<KClass<out Permission>> = emptyList(),
    unlockScreen: Boolean = false,
    closeSystemDialogs: Boolean = false,
    turnScreenOn: Boolean = false,
    enabled: Boolean = true,
    icon: ActionIcon
  ) : this(id.value, title, permissions, unlockScreen, closeSystemDialogs, turnScreenOn, enabled, icon)
}

fun interface ActionIcon {
  @Composable fun Content()
}

abstract class ActionId(val value: String)

fun interface ActionExecutor<I : ActionId> {
  suspend fun execute()
}

interface ActionFactory {
  suspend fun createAction(id: String): Action<*>?

  suspend fun createExecutor(id: String): ActionExecutor<*>?
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ActionSettingsScreen<I : ActionId>

interface ActionPickerDelegate {
  val baseId: String
  val title: String
  val settingsScreen: Screen<Unit>? get() = null
  val icon: @Composable () -> Unit

  suspend fun pickAction(navigator: Navigator): ActionPickerScreen.Result?
}

@Provide object ActionModule {
  @Provide fun <@AddOn T : Action<I>, I : ActionId> actionBinding(
    id: I,
    provider: () -> T,
  ): Pair<String, () -> Action<*>> = id.value to provider

  @Provide fun <@AddOn T : ActionFactory> actionFactoryBinding(
    provider: () -> T
  ): () -> ActionFactory = provider

  @Provide fun <@AddOn T : ActionExecutor<I>, I : ActionId> actionExecutorBinding(
    id: I,
    provider: () -> T
  ): Pair<String, () -> ActionExecutor<*>> = id.value to provider

  @Provide fun <@AddOn T : ActionPickerDelegate> actionPickerDelegateBinding(
    provider: () -> T
  ): () -> ActionPickerDelegate = provider

  @Provide fun <@AddOn T : @ActionSettingsScreen<I> Screen<Unit>, I : ActionId> actionSettingsKeyBinding(
    id: I,
    provider: () -> T,
  ): Pair<String, () -> @ActionSettingsScreen<ActionId> Screen<Unit>> = id.value to provider
}

const val ACTION_DELIMITER = "=:="
