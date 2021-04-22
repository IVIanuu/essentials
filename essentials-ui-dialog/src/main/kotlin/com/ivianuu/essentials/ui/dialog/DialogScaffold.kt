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

package com.ivianuu.essentials.ui.dialog

import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.core.*

@Composable
fun DialogScaffold(
    modifier: Modifier = Modifier,
    dismissible: Boolean = true,
    onDismissRequest: () -> Unit = defaultDismissRequestHandler,
    dialog: @Composable () -> Unit,
) {
    if (!dismissible) {
        BackHandler { }
    }

    Box(
        modifier = Modifier
            .pointerInput(true) {
                detectTapGestures { onDismissRequest() }
            }
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        InsetsPadding {
            Box(
                modifier = Modifier
                    .pointerInput(true) { detectTapGestures {  } }
                    .wrapContentSize(align = Alignment.Center)
                    .animationElement(DialogAnimationElementKey),
                contentAlignment = Alignment.Center
            ) {
                dialog()
            }
        }
    }
}

val DialogAnimationElementKey = Any()

private val defaultDismissRequestHandler: () -> Unit
    @Composable get() {
        val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current!!
        return {
            backPressedDispatcherOwner.onBackPressedDispatcher
                .onBackPressed()
        }
    }
