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
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.ui.dialog.DialogNavigationOptionsBinding
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.popTopKeyWithResult
import com.ivianuu.injekt.FunBinding

class FabPositionKey(val preselect: FabPosition)

@DialogNavigationOptionsBinding<FabPositionKey>
@KeyUiBinding<FabPositionKey>
@FunBinding
@Composable
fun FabPositionDialog(
    key: FabPositionKey,
    popTopKeyWithResult: popTopKeyWithResult<FabPosition>,
) {
    DialogWrapper {
        SingleChoiceListDialog(
            items = FabPosition.values().toList(),
            selectedItem = key.preselect,
            onSelect = { popTopKeyWithResult(it) },
            item = { Text(it.name) }
        )
    }
}