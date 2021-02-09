/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.AmbientUiComponent
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.dialog.DialogNavigationOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.*
import com.ivianuu.essentials.ui.navigation.NavigationOptionFactoryBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get

@Composable
fun DialogListItem(
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dialog: @Composable (dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val component = AmbientUiComponent.current.get<DialogListItemComponent>()
    ListItem(
        modifier = modifier,
        title = title?.let { { title() } },
        subtitle = subtitle?.let { { subtitle() } },
        leading = leading?.let { { leading() } },
        trailing = trailing?.let { { trailing() } },
        onClick = {
            component.dispatchNavigationAction(
                Push(
                    DialogListItemKey {
                        dialog {
                            component.dispatchNavigationAction(PopTop())
                        }
                    }
                )
            )
        }
    )
}

data class DialogListItemKey(val dialog: @Composable () -> Unit)

@KeyUiBinding<DialogListItemKey>
@GivenFun
@Composable
fun DialogListScreen(@Given key: DialogListItemKey) {
    DialogWrapper { key.dialog() }
}

@NavigationOptionFactoryBinding
@Given
val dialogListScreenNavigationsOptionsFactory get() =
    DialogNavigationOptionsFactory<DialogListItemKey>()

@ComponentElementBinding<UiComponent>
@Given class DialogListItemComponent(
    @Given val dispatchNavigationAction: DispatchAction<NavigationAction>
)
