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

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.FabPosition
import androidx.compose.material.Text
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.dialog.DialogNavigationOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationOptionFactoryBinding
import com.ivianuu.essentials.ui.navigation.popWithResult
import com.ivianuu.injekt.Given

class FabPositionKey(val preselect: FabPosition)

@KeyUiBinding<FabPositionKey>
@Given
fun fabPositionUi(
    @Given key: FabPositionKey,
    @Given navigator: DispatchAction<NavigationAction>,
): KeyUi = {
    DialogWrapper {
        SingleChoiceListDialog(
            items = FabPosition.values().toList(),
            selectedItem = key.preselect,
            onSelectionChanged = { navigator.popWithResult(it) },
            item = { Text(it.name) }
        )
    }
}

@NavigationOptionFactoryBinding
@Given
val fabPositionDialogNavigationOptionsFactory = DialogNavigationOptionsFactory<FabPositionKey>()
