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

import androidx.compose.remember
import androidx.ui.layout.Center
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

val TimerRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = "Timer") },
        body = {
            Center {
                val value = collect(remember { timerFlow() })

                Text(
                    text = "Value: $value",
                    style = MaterialTheme.typography().h1
                )
            }
        }
    )
}

private fun timerFlow() = flow {
    var i = 0
    while (true) {
        ++i
        emit(i)
        delay(1000)
    }
}