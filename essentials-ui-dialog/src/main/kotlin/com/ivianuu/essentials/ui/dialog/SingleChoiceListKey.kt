package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.remember
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.PopTop
import com.ivianuu.essentials.ui.navigation.NavigationOptionFactoryBinding
import com.ivianuu.essentials.ui.navigation.popWithResult
import com.ivianuu.injekt.Given

data class SingleChoiceListKey(
    val items: List<Item>,
    val selectedItem: Any,
    val title: String
) {
    data class Item(val value: Any, val title: String)
}

@KeyUiBinding<SingleChoiceListKey>
@Given
fun singleChoiceListUi(
    @Given key: SingleChoiceListKey,
    @Given navigator: DispatchAction<NavigationAction>
): KeyUi = {
    DialogWrapper {
        SingleChoiceListDialog(
            items = remember {
                key.items
                    .map { it.value }
            },
            selectedItem = key.selectedItem,
            onSelectionChanged = { navigator.popWithResult(it) },
            item = { item ->
                Text(key.items.single { it.value == item }.title)
            },
            title = { Text(key.title) },
            negativeButton = {
                TextButton(onClick = { navigator(PopTop()) }) {
                    Text(R.string.es_cancel)
                }
            }
        )
    }
}

@NavigationOptionFactoryBinding
@Given
val singleChoiceListDialogNavigationOptionsFactory = DialogNavigationOptionsFactory<SingleChoiceListKey>()
