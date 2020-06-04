package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.Composable
import androidx.ui.res.stringResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.common.RenderAsyncList
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.injekt.Provider

fun ActionPickerRoute(
    showDefaultOption: Boolean = true,
    showNoneOption: Boolean = true
) = Route {
    SimpleScreen(title = stringResource(R.string.es_action_picker_title)) {
        val viewModelFactory = inject<@Provider (Boolean, Boolean) -> ActionPickerViewModel>()
        val viewModel = viewModel { viewModelFactory(showDefaultOption, showNoneOption) }

        RenderAsyncList(
            state = viewModel.state.items,
            successItemCallback = { _, item ->
                ActionPickerItem(
                    item = item,
                    onClick = { viewModel.itemClicked(item) }
                )
            }
        )
    }
}

sealed class ActionPickerResult {
    data class Action(val actionKey: String) : ActionPickerResult()
    object Default : ActionPickerResult()
    object None : ActionPickerResult()
}

@Composable
private fun ActionPickerItem(
    onClick: () -> Unit,
    item: ActionPickerItem
) {
    ListItem(
        leading = { item.icon() },
        title = { Text(item.title) },
        onClick = onClick
    )
}
