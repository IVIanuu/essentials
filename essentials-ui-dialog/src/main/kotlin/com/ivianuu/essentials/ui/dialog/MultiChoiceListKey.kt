package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Given

class MultiChoiceListKey<T : Any>(
    val items: List<Item<T>>,
    val selectedItems: Set<T>,
    val title: String
) : Key<Set<T>> {
    data class Item<T>(val value: T, val title: String)
}

@Given
fun multiChoiceListUi(
    @Given key: MultiChoiceListKey<Any>,
    @Given navigator: Sink<NavigationAction>
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
