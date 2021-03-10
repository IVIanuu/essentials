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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElement
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElementStackTransition
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.injekt.Given
@HomeItemBinding
@Given
val sharedElementsHomeItem = HomeItem("Shared element") { SharedElementKey(it) }

class SharedElementKey(val color: Color) : Key<Nothing>

@Given
val sharedElementKeyModule = KeyModule<SharedElementKey>()

@Given
fun sharedElementUi(@Given key: SharedElementKey): KeyUi<SharedElementKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Shared Elements") }) }
    ) {
        Box {
            SharedElement(tag = "b", modifier = Modifier.center()) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(key.color, CircleShape)
                )
            }
        }
    }
}

@Given
fun sharedElementsKeyUiOptionsFactory(): KeyUiOptionsFactory<SharedElementKey> = {
    KeyUiOptions(
        enterTransition = SharedElementStackTransition("Shared element" to "b"),
        exitTransition = SharedElementStackTransition("Shared element" to "b")
    )
}
