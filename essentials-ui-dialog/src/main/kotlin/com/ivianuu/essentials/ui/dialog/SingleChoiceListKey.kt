package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        var selectedItem by remember { mutableStateOf(key.selectedItem) }

        SingleChoiceListDialog(
            items = remember {
                key.items
                    .map { it.value }
            },
            selectedItem = selectedItem,
            onSelectionChanged = { selectedItem = it },
            item = { item ->
                Text(key.items.single { it.value == item }.title)
            },
            title = { Text(key.title) },
            positiveButton = {
                TextButton(
                    onClick = { navigator.popWithResult(selectedItem) }
                ) { Text(R.string.es_ok) }
            },
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
