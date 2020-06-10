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

import androidx.ui.core.Modifier
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route

val DrawerRoute = Route(enterTransition = FadeStackTransition()) {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Drawer") }) },
        drawerContent = {
            Surface(color = Color.Blue) {
                Text(
                    text = "Drawer",
                    textStyle = MaterialTheme.typography.h4,
                    modifier = Modifier.center()
                )
            }
        },
        body = {
            Surface(color = Color.Red) {
                Text(
                    text = "Body",
                    textStyle = MaterialTheme.typography.h4,
                    modifier = Modifier.center()
                )
            }
        }
    )
}