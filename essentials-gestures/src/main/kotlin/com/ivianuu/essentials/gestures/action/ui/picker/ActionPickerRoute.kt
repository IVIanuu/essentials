package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.Composable
import androidx.ui.res.stringResource
import com.ivianuu.essentials.android.ui.common.RenderAsyncList
import com.ivianuu.essentials.android.ui.common.SimpleScreen
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.material.ListItem
import com.ivianuu.essentials.android.ui.navigation.Route
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.mvrx.injectMvRxViewModel
import com.ivianuu.injekt.parametersOf

fun ActionPickerRoute(
    showDefaultOption: Boolean = true,
    showNoneOption: Boolean = true
) = Route {
    SimpleScreen(title = stringResource(R.string.es_action_picker_title)) {
        val viewModel = injectMvRxViewModel<ActionPickerViewModel>(
            parameters = parametersOf(
                showDefaultOption,
                showNoneOption
            )
        )

        RenderAsyncList(
            state = viewModel.getCurrentState().items,
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
