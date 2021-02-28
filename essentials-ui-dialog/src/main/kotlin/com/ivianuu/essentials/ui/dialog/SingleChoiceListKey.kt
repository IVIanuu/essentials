package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.remember
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module

data class SingleChoiceListKey<T : Any>(
    val items: List<Item<T>>,
    val selectedItem: T,
    val title: String
) : Key<T> {
    data class Item<T : Any>(val value: T, val title: String)
}

@Module
fun <T : Any> singleChoiceListKeyModule() = KeyModule<SingleChoiceListKey<T>>()

@Given
fun <T : Any> singleChoiceListUi(
    @Given key: SingleChoiceListKey<T>,
    @Given navigator: DispatchAction<NavigationAction>
): KeyUi<T> = {
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
                    Text(R.string.es_cancel)
                }
            }
        )
    }
}

@Given
fun <T : Any> singleChoiceListUiOptionsFactory() =
    DialogKeyUiOptionsFactory<SingleChoiceListKey<T>>()
