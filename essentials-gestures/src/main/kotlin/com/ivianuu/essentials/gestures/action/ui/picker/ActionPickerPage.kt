package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.viewmodel.currentState
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@Reader
@Composable
fun ActionPickerPage(
    showDefaultOption: Boolean = true,
    showNoneOption: Boolean = true
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_action_picker_title) }) }
    ) {
        val viewModel = viewModel {
            given<(Boolean, Boolean) -> ActionPickerViewModel>()(
                showDefaultOption,
                showNoneOption
            )
        }

        ResourceLazyColumnItems(
            resource = viewModel.currentState.items
        ) { item ->
            ActionPickerItem(
                item = item,
                onClick = { viewModel.itemClicked(item) }
            )
        }
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
