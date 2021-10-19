package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.singleActionIcon
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultDispatcher
import kotlinx.coroutines.withContext

@Provide class ActionRepository(
  private val actions: () -> Map<String, () -> Action<*>> = { emptyMap() },
  private val actionFactories: () -> List<() -> ActionFactory> = { emptyList() },
  private val actionsExecutors: () -> Map<String, () -> ActionExecutor<*>> = { emptyMap() },
  private val actionSettings: () -> Map<String, () -> ActionSettingsKey<*>> = { emptyMap() },
  private val actionPickerDelegates: () -> List<() -> ActionPickerDelegate> = { emptyList() },
  private val dispatcher: DefaultDispatcher,
  private val rp: ResourceProvider,
  private val toaster: Toaster
) {
  suspend fun getAllActions(): List<Action<*>> = withContext(dispatcher) {
    actions().values.map { it() }
  }

  suspend fun getAction(id: String): Action<*> = withContext(dispatcher) {
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
        icon = singleActionIcon(R.drawable.es_ic_error)
      )
  }

  suspend fun getActionExecutor(id: String): ActionExecutor<*> = withContext(dispatcher) {
    actionsExecutors()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createExecutor(id)
      ?: {
        showToast(R.string.es_error_action)
      }
  }

  suspend fun getActionSettingsKey(id: String): ActionSettingsKey<*>? =
    withContext(dispatcher) { actionSettings()[id]?.invoke() }

  suspend fun getActionPickerDelegates(): List<ActionPickerDelegate> =
    withContext(dispatcher) { actionPickerDelegates().map { it() } }
}
