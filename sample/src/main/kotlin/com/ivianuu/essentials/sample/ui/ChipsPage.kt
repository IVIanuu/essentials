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

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.FunBinding

@OptIn(ExperimentalLayout::class)
@FunBinding
@Composable
fun ChipsPage(showToast: showToast) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Chips") }) }
    ) {
        Box(
            modifier = Modifier.padding(all = 8.dp),
            alignment = Alignment.TopStart
        ) {
            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                remember { Names.shuffled() }.forEach { name ->
                    Chip(
                        name = name,
                        onClick = { showToast("Clicked $name") }
                    )
                }
            }
        }
    }
}

@Composable
private fun Chip(
    onClick: () -> Unit,
    name: String
) {
    key(name) {
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
                    .clickable(
                        onClick = onClick,
                        indication = RippleIndication(bounded = false)
                    ),
                alignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.body2,
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
