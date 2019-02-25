/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.ui.list

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global configurations
 */
object ListPlugins

private val defaultExecutor = Executors.newSingleThreadExecutor()

private var _defaultDiffingExecutor: Executor = defaultExecutor

var ListPlugins.defaultDiffingExecutor: Executor
    get() = _defaultDiffingExecutor
    set(value) {
        _defaultDiffingExecutor = value
    }

private var _defaultBuildingExecutor: Executor = defaultExecutor

var ListPlugins.defaultBuildingExecutor: Executor
    get() = _defaultBuildingExecutor
    set(value) {
        _defaultBuildingExecutor = value
    }