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

package com.ivianuu.essentials.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableContract
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy

@Composable
inline fun <T> rememberState(
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember { mutableStateOf(init(), policy) }

@Composable
inline fun <T, V1> rememberState(
    v1: V1,
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember(v1) { mutableStateOf(init()) }

@Composable
inline fun <T, reified V1, reified V2> rememberState(
    v1: V1,
    v2: V2,
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember(v1, v2) { mutableStateOf(init()) }

@Composable
inline fun <T> rememberState(
    vararg inputs: Any?,
    init: @ComposableContract(preventCapture = true) () -> T,
) = remember(*inputs) { mutableStateOf(init()) }
