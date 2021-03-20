package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given

data class MultiChoiceListKey(
    val items: List<String>,
    val selectedItems: Set<String>,
    val title: String
) : Key<Set<String>>

@Given
val multiChoiceListKeyModule = KeyModule<MultiChoiceListKey>()

@Given
fun <T : Any> multiChoiceListUi(
    @Given key: MultiChoiceListKey,
    @Given navigator: Collector<NavigationAction>
): KeyUi<T> = {
    DialogWrapper {
        var selectedItems by remember { mutableStateOf(key.selectedItems) }

        MultiChoiceListDialog(
            items = key.items,
            selectedItems = selectedItems,
            onSelectionsChanged = { selectedItems = it },
            item = { Text(it) },
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
val multiChoiceListUiOptionsFactory = DialogKeyUiOptionsFactory<MultiChoiceListKey>()
