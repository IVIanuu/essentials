package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.singleActionIcon
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultDispatcher
import kotlinx.coroutines.withContext

interface ActionRepository {
  suspend fun getAllActions(): List<Action<*>>

  suspend fun getAction(id: String): Action<*>

  suspend fun getActionExecutor(id: String): ActionExecutor<*>

  suspend fun getActionSettingsKey(id: String): ActionSettingsKey<*>?

  suspend fun getActionPickerDelegates(): List<ActionPickerDelegate>
}

@Provide class ActionRepositoryImpl(
  private val actions: () -> Map<String, () -> Action<*>>,
  private val actionFactories: () -> List<() -> ActionFactory>,
  private val actionsExecutors: () -> Map<String, () -> ActionExecutor<*>>,
  private val actionSettings: () -> Map<String, () -> ActionSettingsKey<*>>,
  private val actionPickerDelegates: () -> List<() -> ActionPickerDelegate>,
  private val dispatcher: DefaultDispatcher,
  private val T: ToastContext
) : ActionRepository {
  override suspend fun getAllActions() = withContext(dispatcher) {
    actions().values.map { it() }
  }

  override suspend fun getAction(id: String) = withContext(dispatcher) {
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

  override suspend fun getActionExecutor(id: String) = withContext(dispatcher) {
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

  override suspend fun getActionSettingsKey(id: String) =
    withContext(dispatcher) { actionSettings()[id]?.invoke() }

  override suspend fun getActionPickerDelegates() =
    withContext(dispatcher) { actionPickerDelegates().map { it() } }
}
