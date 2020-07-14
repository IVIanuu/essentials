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
import com.ivianuu.essentials.gestures.action.getAction
import com.ivianuu.essentials.gestures.action.getActions
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.viewmodel.StateViewModel
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.launch

@Reader
@Given
internal class ActionPickerViewModel(
    private val showDefaultOption: Boolean,
    private val showNoneOption: Boolean
) : StateViewModel<ActionPickerState>(ActionPickerState()) {

    init {
        execute(
            block = {
                val specialOptions = mutableListOf<ActionPickerItem.SpecialOption>()

                if (showDefaultOption) {
                    specialOptions += ActionPickerItem.SpecialOption(
                        title = Resources.getString(R.string.es_default),
                        getResult = { ActionPickerResult.Default }
                    )
                }

                if (showNoneOption) {
                    specialOptions += ActionPickerItem.SpecialOption(
                        title = Resources.getString(R.string.es_none),
                        getResult = { ActionPickerResult.None }
                    )
                }

                val actionsAndDelegates = ((given<Set<ActionPickerDelegate>>()
                    .map {
                        ActionPickerItem.PickerDelegate(
                            it,
                            navigator
                        )
                    }) + (getActions().map { ActionPickerItem.ActionItem(it) }))
                    .sortedBy { it.title }

                return@execute specialOptions + actionsAndDelegates
            },
            reducer = { copy(items = it) }
        )
    }

    fun itemClicked(selectedItem: ActionPickerItem) {
        scope.launch {
            val result = selectedItem.getResult() ?: return@launch
            if (result is ActionPickerResult.Action) {
                val action = getAction(result.actionKey)
                if (!given<PermissionManager>().request(action.permissions)) return@launch
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
data class ActionPickerState(val items: Resource<List<ActionPickerItem>> = Idle)
