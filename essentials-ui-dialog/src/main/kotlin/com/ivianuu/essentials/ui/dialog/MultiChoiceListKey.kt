package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

data class MultiChoiceListKey<T : Any>(
    val items: List<Item<T>>,
    val selectedItems: Set<T>,
    val title: String
) : Key<Set<T>> {
    data class Item<T>(val value: T, val title: String)
}

@Given
fun multiChoiceListUi(
    @Given key: MultiChoiceListKey<Any>,
    @Given navigator: Navigator
): KeyUi<MultiChoiceListKey<Any>> = {
    DialogScaffold {
        var selectedItems by remember { mutableStateOf(key.selectedItems) }

        MultiChoiceListDialog(
            items = remember {
                key.items
                    .map { it.value }
            },
            selectedItems = selectedItems,
            onSelectionsChanged = { selectedItems = it },
            item = { item ->
                Text(key.items.single { it.value == item }.title)
            },
            title = { Text(key.title) },
            positiveButton = {
                TextButton(
                    onClick = { navigator.pop(key, selectedItems) }
                ) { Text(stringResource(R.string.es_ok)) }
            },
            negativeButton = {
                TextButton(onClick = { navigator.pop(key, null) }) {
                    Text(stringResource(R.string.es_cancel))
                }
            }
        )
    }
}

@Given
val multiChoiceListUiOptionsFactory = DialogKeyUiOptionsFactory<MultiChoiceListKey<Any>>()
