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

package com.ivianuu.essentials.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding

@UiDecoratorBinding("ui_logger", dependents = ["system_bars"])
@FunBinding
@Composable
fun UiLogger(logger: Logger, @FunApi content: @Composable () -> Unit) {
    onActive {
        logger.d("hello from ui")
        onDispose {
            logger.d("bye from ui")
        }
    }
    content()
}
