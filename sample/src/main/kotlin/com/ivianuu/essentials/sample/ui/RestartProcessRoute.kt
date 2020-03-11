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

import androidx.ui.layout.Center
import com.ivianuu.essentials.android.ui.common.SimpleScreen
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.android.ui.material.Button
import com.ivianuu.essentials.android.ui.navigation.Route
import com.ivianuu.essentials.processrestart.ProcessRestarter

val RestartProcessRoute = Route {
    SimpleScreen(title = "Restart process") {
        Center {
            val processRestarter = inject<ProcessRestarter>()
            Button(onClick = { processRestarter.restartProcess() }) {
                Text("Restart process")
            }
        }
    }
}