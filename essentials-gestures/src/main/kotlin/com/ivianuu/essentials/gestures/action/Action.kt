/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey

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
