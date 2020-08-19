package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Text
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.injekt.Reader

@Reader
@Composable
fun ActionPickerPage(
    showDefaultOption: Boolean = true,
    showNoneOption: Boolean = true
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_action_picker_title) }) }
    ) {
        val (state, dispatch) = rememberStore(
            showDefaultOption, showNoneOption
        ) { actionPickerStore(showDefaultOption, showNoneOption) }
        ResourceLazyColumnItems(state.items) { item ->
            ActionPickerItem(
                item = item,
                onClick = { dispatch(ActionPickerAction.ItemClicked(item)) }
            )
        }
    }
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
