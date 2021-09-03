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
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.Flow

data class Action<I : ActionId>(
  val id: String,
  val title: String,
  val permissions: List<TypeKey<Permission>> = emptyList(),
  val unlockScreen: Boolean = false,
  val enabled: Boolean = true,
  val icon: Flow<ActionIcon>
) {
  constructor(
    id: I,
    title: String,
    permissions: List<TypeKey<Permission>> = emptyList(),
    unlockScreen: Boolean = false,
    enabled: Boolean = true,
    icon: Flow<ActionIcon>
  ) : this(id.value, title, permissions, unlockScreen, enabled, icon)
}

typealias ActionIcon = @Composable () -> Unit

abstract class ActionId(val value: String)

@Provide fun <@Spread T : Action<I>, I : ActionId> actionPair(
  id: I,
  provider: () -> T,
): Pair<String, () -> Action<I>> = id.value to provider

typealias ActionExecutor<I> = suspend () -> Unit

@Provide fun <@Spread T : ActionExecutor<I>, I : ActionId> actionExecutorPair(
  id: I,
  provider: () -> T
): Pair<String, () -> ActionExecutor<*>> = id.value to provider

interface ActionFactory {
  suspend fun handles(id: String): Boolean
  suspend fun createAction(id: String): Action<*>
  suspend fun createExecutor(id: String): ActionExecutor<*>
}

typealias ActionSettingsKey<I> = Key<Nothing>

@Provide fun <@Spread T : ActionSettingsKey<I>, I : ActionId> actionSettingsKeyPair(
  id: I,
  provider: () -> T,
): Pair<String, () -> ActionSettingsKey<*>> = id.value to provider

interface ActionPickerDelegate {
  val baseId: String
  val title: String
  val icon: @Composable () -> Unit
  val settingsKey: Key<Nothing>? get() = null
  suspend fun pickAction(): ActionPickerKey.Result?
}

typealias FloatingWindowActionsEnabled = Boolean
@Provide val floatingWindowActionsEnabled: FloatingWindowActionsEnabled = true

const val ACTION_DELIMITER = "=:="
