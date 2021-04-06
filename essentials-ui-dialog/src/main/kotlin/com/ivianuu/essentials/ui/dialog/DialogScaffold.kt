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

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.ivianuu.essentials.ui.core.InsetsPadding

@Composable
fun DialogScaffold(
    dismissible: Boolean = true,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = defaultDismissRequestHandler,
    dialog: @Composable () -> Unit,
) {
    if (!dismissible) {
        BackHandler { }
    }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
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
                    .pointerInput(Unit) { detectTapGestures {  } }
                    .wrapContentSize(align = Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                dialog()
            }
        }
    }
}

private val defaultDismissRequestHandler: () -> Unit
    @Composable get() {
        val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
        return {
            backPressedDispatcherOwner.onBackPressedDispatcher
                .onBackPressed()
        }
    }