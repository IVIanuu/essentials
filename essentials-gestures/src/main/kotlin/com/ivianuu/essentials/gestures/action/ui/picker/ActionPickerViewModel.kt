package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.ui.core.Modifier
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredSize
import androidx.ui.unit.dp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionStore
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import kotlinx.coroutines.launch

@Factory
internal class ActionPickerViewModel(
    @Param private val showDefaultOption: Boolean,
    @Param private val showNoneOption: Boolean,
    private val actionStore: ActionStore,
    private val actionPickerDelegates: Set<ActionPickerDelegate>,
    private val navigator: NavigatorState,
    private val permissionManager: PermissionManager,
    private val resourceProvider: ResourceProvider
) : MvRxViewModel<ActionPickerState>(ActionPickerState()) {

    init {
        coroutineScope.execute(
            block = {
                val specialOptions = mutableListOf<ActionPickerItem.SpecialOption>()

                if (showDefaultOption) {
                    specialOptions += ActionPickerItem.SpecialOption(
                        title = resourceProvider.getString(R.string.es_default),
                        getResult = { ActionPickerResult.Default }
                    )
                }

                if (showNoneOption) {
                    specialOptions += ActionPickerItem.SpecialOption(
                        title = resourceProvider.getString(R.string.es_none),
                        getResult = { ActionPickerResult.None }
                    )
                }

                val actionsAndDelegates = ((actionPickerDelegates
                    .map {
                        ActionPickerItem.PickerDelegate(
                            it,
                            navigator
                        )
                    }) + (actionStore.getActions().map { ActionPickerItem.ActionItem(it) }))
                    .sortedBy { it.title }

                return@execute specialOptions + actionsAndDelegates
            },
            reducer = { copy(items = it) }
        )
    }

    fun itemClicked(selectedItem: ActionPickerItem) {
        coroutineScope.launch {
            val result = selectedItem.getResult()
            if (result is ActionPickerResult.Action) {
                val action = actionStore.getAction(result.actionKey)
                if (!permissionManager.request(action.permissions)) return@launch
            }

            navigator.popTop(result = result)
        }
    }
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
        val navigator: NavigatorState
    ) : ActionPickerItem() {
        override val title: String
            get() = delegate.title

        @Composable
        override fun icon() {
            delegate.icon()
        }

        override suspend fun getResult() = delegate.getResult(navigator)
    }

    @Immutable
    data class SpecialOption(
        override val title: String,
        val getResult: () -> ActionPickerResult?
    ) : ActionPickerItem() {

        @Composable
        override fun icon() {
            Spacer(Modifier.preferredSize(24.dp))
        }

        override suspend fun getResult() = getResult.invoke()
    }

    abstract val title: String

    @Composable
    abstract fun icon()

    abstract suspend fun getResult(): ActionPickerResult?
}

@Immutable
data class ActionPickerState(val items: Async<List<ActionPickerItem>> = Uninitialized)
