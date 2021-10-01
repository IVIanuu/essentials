package com.ivianuu.essentials.gestures.action

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultDispatcher
import kotlinx.coroutines.withContext

@Provide class ActionRepository(
  private val actions: Map<String, () -> Action<*>> = emptyMap(),
  private val actionFactories: Set<() -> ActionFactory> = emptySet(),
  private val actionsExecutors: Map<String, () -> ActionExecutor<*>> = emptyMap(),
  private val actionSettings: Map<String, () -> ActionSettingsKey<*>> = emptyMap(),
  private val actionPickerDelegates: Set<() -> ActionPickerDelegate> = emptySet(),
  private val dispatcher: DefaultDispatcher
) {
  suspend fun getAllActions(): List<Action<*>> = withContext(dispatcher) {
    actions.values.map { it() }
  }

  suspend fun getAction(id: String): Action<*>? = withContext(dispatcher) {
    actions[id]
      ?.invoke()
      ?: actionFactories
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createAction(id)
  }

  suspend fun getActionExecutor(id: String): ActionExecutor<*>? = withContext(dispatcher) {
    actionsExecutors[id]
      ?.invoke()
      ?: actionFactories
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createExecutor(id)
  }

  suspend fun getActionSettingsKey(id: String): ActionSettingsKey<*>? =
    withContext(dispatcher) { actionSettings[id]?.invoke() }

  suspend fun getActionPickerDelegates(): List<ActionPickerDelegate> =
    withContext(dispatcher) { actionPickerDelegates.map { it() } }
}
