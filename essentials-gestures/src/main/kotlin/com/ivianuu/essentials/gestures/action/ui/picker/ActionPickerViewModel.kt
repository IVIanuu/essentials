package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.ui.core.Modifier
import androidx.ui.layout.Spacer
import androidx.ui.layout.size
import androidx.ui.unit.dp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionStore
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.ui.Async
import com.ivianuu.essentials.ui.Uninitialized
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.launch

@Transient
internal class ActionPickerViewModel(
    private val showDefaultOption: @Assisted Boolean,
    private val showNoneOption: @Assisted Boolean,
    private val actionStore: ActionStore,
    private val actionPickerDelegates: Set<ActionPickerDelegate>,
    dispatchers: AppCoroutineDispatchers,
    private val navigator: Navigator,
    private val permissionManager: PermissionManager,
    private val resourceProvider: ResourceProvider
) : MvRxViewModel<ActionPickerState>(ActionPickerState(), dispatchers) {

    init {
        scope.execute(
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
        scope.launch {
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
        val navigator: Navigator
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
data class ActionPickerState(val items: Async<List<ActionPickerItem>> = Uninitialized())
