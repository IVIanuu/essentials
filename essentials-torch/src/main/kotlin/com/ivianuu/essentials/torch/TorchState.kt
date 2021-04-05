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

package com.ivianuu.essentials.torch

import com.ivianuu.essentials.store.FeatureBuilder
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.store.actionsOf
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope

data class TorchState(val torchEnabled: Boolean = false) : State()

sealed class TorchAction {
    data class UpdateTorchEnabled(val value: Boolean) : TorchAction()
}

@Given
val torchFeature: FeatureBuilder<AppGivenScope, TorchState, TorchAction> = {
    actionsOf<UpdateTorchEnabled>()
        .updateIn(this) { copy(torchEnabled = it.value) }
}
