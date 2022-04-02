/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.staticActionIcon
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.withContext

interface ActionRepository {
  suspend fun getAllActions(): List<Action<*>>

  suspend fun getAction(id: String): Action<*>

  suspend fun getActionExecutor(id: String): ActionExecutor<*>

  suspend fun getActionSettingsKey(id: String): Key<Unit>?

  suspend fun getActionPickerDelegates(): List<ActionPickerDelegate>
}

@Provide class ActionRepositoryImpl(
  private val actions: () -> Map<String, () -> Action<*>>,
  private val actionFactories: () -> List<() -> ActionFactory>,
  private val actionsExecutors: () -> Map<String, () -> ActionExecutor<*>>,
  private val actionSettings: () -> Map<String, () -> @ActionSettingsKey<ActionId> Key<Unit>>,
  private val actionPickerDelegates: () -> List<() -> ActionPickerDelegate>,
  private val context: DefaultContext,
  private val T: ToastContext
) : ActionRepository {
  override suspend fun getAllActions() = withContext(context) {
    actions().values.map { it() }
  }

  override suspend fun getAction(id: String) = withContext(context) {
    actions()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createAction(id)
      ?: Action(
        id = "error",
        title = loadResource(R.string.es_error_action),
        icon = staticActionIcon(R.drawable.es_ic_error)
      )
  }

  override suspend fun getActionExecutor(id: String) = withContext(context) {
    actionsExecutors()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createExecutor(id)
      ?: ActionExecutor {
        showToast(R.string.es_error_action)
      }
  }

  override suspend fun getActionSettingsKey(id: String) =
    withContext(context) { actionSettings()[id]?.invoke() }

  override suspend fun getActionPickerDelegates() =
    withContext(context) { actionPickerDelegates().map { it() } }
}
