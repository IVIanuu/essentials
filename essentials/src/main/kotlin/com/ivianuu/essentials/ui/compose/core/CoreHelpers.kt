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

package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Composable
import androidx.compose.Composer
import androidx.compose.composer

fun composable(
    key: Any,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            startGroup(key)
            startGroup(invocation)
            block()
            endGroup()
            endGroup()
        }
    }
}

fun <V1> composable(
    key: Any,
    v1: V1,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            startGroup(key)
            if (changed(v1) || inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
            endGroup()
        }
    }
}

fun <V1, V2> composable(
    key: Any,
    v1: V1,
    v2: V2,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            startGroup(key)
            if (changed(v1) || changed(v2) || inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
            endGroup()
        }
    }
}

fun composable(
    key: Any,
    vararg inputs: Any?,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            startGroup(key)
            if (changed(inputs.contentHashCode()) || inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
            endGroup()
        }
    }
}

fun staticComposable(key: Any, block: @Composable() () -> Unit) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            startGroup(key)
            if (inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
            endGroup()
        }
    }
}

private fun Composer<*>.wrapInRestartScope(key: Any, block: @Composable() () -> Unit) {
    startRestartGroup(key)
    block()
    endRestartGroup()?.updateScope { wrapInRestartScope(key, block) }
}

private val invocation = Any()