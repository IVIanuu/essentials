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
import com.ivianuu.essentials.ui.navigation.pushForResult
import com.ivianuu.injekt.Given

data class SingleChoiceListKey(
    val items: List<String>,
    val selectedItem: String,
    val title: String
) : Key<String>

@Given
val singleChoiceListKeyModule = KeyModule<SingleChoiceListKey>()

@Given
fun <T : Any> singleChoiceListUi(
    @Given key: SingleChoiceListKey,
    @Given navigator: Collector<NavigationAction>
): KeyUi<T> = {
    DialogWrapper {
        SingleChoiceListDialog(
            items = key.items,
            selectedItem = key.selectedItem,
            onSelectionChanged = { navigator(Pop(key, it)) },
            item = { Text(it) },
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
val singleChoiceListUiOptionsFactory = DialogKeyUiOptionsFactory<SingleChoiceListKey>()
