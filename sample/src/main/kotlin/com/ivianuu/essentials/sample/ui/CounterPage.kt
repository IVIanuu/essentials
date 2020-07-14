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
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.material.ExtendedFloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Reader
@Reader
@Composable
fun CounterPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Counter") }) }
    ) {
        Column(
            modifier = Modifier.center(),
            verticalArrangement = Arrangement.Center,
            horizontalGravity = Alignment.CenterHorizontally
        ) {
            val (count, setCount) = state { 0 }

            Text(
                text = "Count: $count",
                style = MaterialTheme.typography.h3
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("Inc") },
                onClick = { setCount(count + 1) }
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("dec") },
                onClick = { setCount(count - 1) }
            )
        }
    }
}
