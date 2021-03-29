package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given

class SingleChoiceListKey<T : Any>(
    val items: List<Item<T>>,
    val selectedItem: T,
    val title: String
) : Key<T> {
    data class Item<T : Any>(val value: T, val title: String)
}

@Given
val singleChoiceListKeyModule = KeyModule<SingleChoiceListKey<Any>>()

@Given
fun singleChoiceListUi(
    @Given key: SingleChoiceListKey<Any>,
    @Given navigator: Collector<NavigationAction>
): KeyUi<SingleChoiceListKey<Any>> = {
    DialogWrapper {
        SingleChoiceListDialog(
            items = remember {
                key.items
                    .map { it.value }
            },
            selectedItem = key.selectedItem,
            onSelectionChanged = { navigator(Pop(key, it)) },
            item = { item ->
                Text(key.items.single { it.value == item }.title)
            },
            title = { Text(key.title) },
            negativeButton = {
                TextButton(onClick = { navigator(Pop(key)) }) {
                    Text(stringResource(R.string.es_cancel))
                }
            }
        )
    }
}

@Given
val singleChoiceListUiOptionsFactory = DialogKeyUiOptionsFactory<SingleChoiceListKey<Any>>()

