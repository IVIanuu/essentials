package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.getAction
import com.ivianuu.essentials.gestures.action.getActions
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.ItemClicked
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.ActionItem
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.PickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.SpecialOption
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.store.execute
import com.ivianuu.essentials.util.Resources
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope

@Given
fun CoroutineScope.actionPickerStore(
    showDefaultOption: Boolean,
    showNoneOption: Boolean
) = store<ActionPickerState, ActionPickerAction>(
    ActionPickerState()
) {
    execute(
        block = {
            val specialOptions = mutableListOf<SpecialOption>()

            if (showDefaultOption) {
                specialOptions += SpecialOption(
                    title = Resources.getString(R.string.es_default),
                    getResult = { ActionPickerResult.Default }
                )
            }

            if (showNoneOption) {
                specialOptions += SpecialOption(
                    title = Resources.getString(R.string.es_none),
                    getResult = { ActionPickerResult.None }
                )
            }

            val actionsAndDelegates = ((given<Set<ActionPickerDelegate>>()
                .map {
                    PickerDelegate(
                        it,
                        navigator
                    )
                }) + (getActions().map { ActionItem(it) }))
                .sortedBy { it.title }

            return@execute specialOptions + actionsAndDelegates
        },
        reducer = { copy(items = it) }
    )

    onEachAction { action ->
        when (action) {
            is ItemClicked -> {
                val result = action.item.getResult() ?: return@onEachAction
                if (result is ActionPickerResult.Action) {
                    val pickedAction = getAction(result.actionKey)
                    if (!requestPermissions(pickedAction.permissions)) return@onEachAction
                }

                navigator.popTop(result = result)
            }
        }.exhaustive
    }
}

sealed class ActionPickerResult {
    data class Action(val actionKey: String) : ActionPickerResult()
    object Default : ActionPickerResult()
    object None : ActionPickerResult()
}

@Immutable
sealed class ActionPickerItem {
    @Immutable
    data class ActionItem(val action: Action) : ActionPickerItem() {
        override val title: String
            get() = action.title

        @Composable
        override fun icon() {
            ActionIcon(action = action)
        }

        override suspend fun getResult() = ActionPickerResult.Action(action.key)
    }

    @Immutable
    data class PickerDelegate(
        val delegate: ActionPickerDelegate,
        val navigator: Navigator
    ) : ActionPickerItem() {
        override val title: String
            get() = delegate.title

        @Composable
        override fun icon() {
            delegate.icon()
        }

        override suspend fun getResult() = delegate.getResult()
    }

    @Immutable
    data class SpecialOption(
        override val title: String,
        val getResult: () -> ActionPickerResult?
    ) : ActionPickerItem() {

        @Composable
        override fun icon() {
            Spacer(Modifier.size(24.dp))
        }

        override suspend fun getResult() = getResult.invoke()
    }

    abstract val title: String

    @Composable
    abstract fun icon()

    abstract suspend fun getResult(): ActionPickerResult?
}

@Immutable
data class ActionPickerState(val items: Resource<List<ActionPickerItem>> = Idle)

sealed class ActionPickerAction {
    data class ItemClicked(val item: ActionPickerItem) : ActionPickerAction()
}
