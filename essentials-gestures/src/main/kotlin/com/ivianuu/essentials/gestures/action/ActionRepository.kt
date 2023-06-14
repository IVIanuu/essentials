/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.staticActionIcon
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.DefaultCoroutineContext
import kotlinx.coroutines.withContext

interface ActionRepository {
  suspend fun getAllActions(): List<Action<*>>

  suspend fun getAction(id: String): Action<*>

  suspend fun getActionExecutor(id: String): ActionExecutor<*>

  suspend fun getActionSettingsKey(id: String): Screen<Unit>?

  suspend fun getActionPickerDelegates(): List<ActionPickerDelegate>
}

@Provide class ActionRepositoryImpl(
  private val actions: () -> Map<String, () -> Action<*>>,
  private val actionFactories: () -> List<() -> ActionFactory>,
  private val actionsExecutors: () -> Map<String, () -> ActionExecutor<*>>,
  private val actionSettings: () -> Map<String, () -> @ActionSettingsKey<ActionId> Screen<Unit>>,
  private val actionPickerDelegates: () -> List<() -> ActionPickerDelegate>,
  private val coroutineContext: DefaultCoroutineContext,
  private val resources: Resources,
  private val toaster: Toaster
) : ActionRepository {
  override suspend fun getAllActions() = withContext(coroutineContext) {
    actions().values.map { it() }
  }

  override suspend fun getAction(id: String) = withContext(coroutineContext) {
    actions()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createAction(id)
      ?: Action(
        id = "error",
        title = resources(R.string.es_error_action),
        icon = staticActionIcon(R.drawable.es_ic_error)
      )
  }

  override suspend fun getActionExecutor(id: String) = withContext(coroutineContext) {
    actionsExecutors()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createExecutor(id)
      ?: ActionExecutor {
        toaster(R.string.es_error_action)
      }
  }

  override suspend fun getActionSettingsKey(id: String) =
    withContext(coroutineContext) { actionSettings()[id]?.invoke() }

  override suspend fun getActionPickerDelegates() =
    withContext(coroutineContext) { actionPickerDelegates().map { it() } }
}
