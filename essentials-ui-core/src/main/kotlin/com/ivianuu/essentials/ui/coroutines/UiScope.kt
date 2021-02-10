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

package com.ivianuu.essentials.ui.coroutines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onDispose
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

typealias UiScope = CoroutineScope

// todo should be scoped to the UiComponent but @UiDecoratorBinding installs in ActivityComponent
// so we also have to scope to ActivityComponent
@Scoped<ActivityComponent> @Given
fun uiScope(
    @Given mainDispatcher: MainDispatcher
): UiScope = CoroutineScope(mainDispatcher)

@UiDecoratorBinding
@GivenFun
@Composable
fun CancelUiScope(
    @Given logger: Logger,
    @Given uiScope: UiScope,
    content: @Composable () -> Unit
) {
    onDispose {
        logger.d { "Cancelling ui scope" }
        uiScope.cancel()
    }
    content()
}
