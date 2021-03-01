package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module

data class MultiChoiceListKey<T : Any>(
    val items: List<Item<T>>,
    val selectedItems: Set<T>,
    val title: String
) : Key<Set<T>> {
    data class Item<T>(val value: T, val title: String)
}

@Module
fun <T : Any> multiChoiceListKeyModule() = KeyModule<MultiChoiceListKey<T>>()

@Given
fun <T : Any> multiChoiceListUi(
    @Given key: MultiChoiceListKey<T>,
    @Given navigator: DispatchAction<NavigationAction>
): KeyUi<T> = {
    DialogWrapper {
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
                    onClick = { navigator(Pop(key, selectedItems)) }
                ) { Text(stringResource(R.string.es_ok)) }
            },
            negativeButton = {
                TextButton(onClick = { navigator(Pop(key)) }) {
                    Text(stringResource(R.string.es_cancel))
                }
            }
        )
    }
}

@Given
fun <T : Any> multiChoiceListUiOptionsFactory() =
    DialogKeyUiOptionsFactory<MultiChoiceListKey<T>>()
