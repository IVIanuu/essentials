/*
 * Copyright 2019 Manuel Wrage
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

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import com.ivianuu.essentials.processrestart.restartProcess
import com.ivianuu.essentials.ui.coroutines.compositionScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped
import kotlinx.coroutines.launch

@Reader
@Unscoped
class RestartProcessPage {
    @Composable
    operator fun invoke() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Restart process") }) }
        ) {
            val scope = compositionScope()
            Button(
                modifier = Modifier.center(),
                onClick = {
                    scope.launch {
                        restartProcess()
                    }
                }
            ) {
                Text("Restart process")
            }
        }
    }
}
