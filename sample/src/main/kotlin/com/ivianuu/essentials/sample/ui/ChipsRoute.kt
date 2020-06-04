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
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.ExperimentalLayout
import androidx.ui.layout.FlowRow
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.RippleIndication
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Toaster

@OptIn(ExperimentalLayout::class)
val ChipsRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Chips") }) },
        body = {
            Box(
                modifier = Modifier.padding(all = 8.dp),
                gravity = ContentGravity.TopStart
            ) {
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    remember { Names.shuffled() }.forEach { name ->
                        Chip(name = name)
                    }
                }
            }
        }
    )
}

@Composable
private fun Chip(name: String) {
    key(name) {
        val toaster = inject<Toaster>()
        val color = remember {
            ColorPickerPalette.values().flatMap { it.colors }.shuffled().first()
        }
        Surface(
            color = color,
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .preferredHeight(32.dp)
                    .clickable(indication = RippleIndication(bounded = false)) { toaster.toast("Clicked $name") },
                gravity = Alignment.Center
            ) {
                Text(
                    text = name,
                    textStyle = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    ),
                )
            }
        }
    }
}

private val Names = listOf(
    "Hans Dieter Josef",
    "Alex Meier",
    "Kim Jong Hun Ching Chang Chong Meier Richard Anderson",
    "Claude Hardy",
    "Wilhelm Richards",
    "Gustova Da Costa",
    "Lisa Wray",
    "Michael Clark",
    "Yusuf Grimes",
    "Mustafa Müller",
    "Diego Ribas Da Cunha",
    "Jan"
)