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

package com.ivianuu.essentials.util

import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shell
 */
@Singleton
class Shell @Inject constructor(private val dispatchers: AppCoroutineDispatchers) {

    suspend fun run(command: String) = run(listOf(command))

    suspend fun run(commands: Collection<String>) =
        run(commands.toTypedArray())

    suspend fun run(commands: Array<String>) = withContext(dispatchers.io) {
        Shell.SU.run(commands).toList()
    }

    suspend fun available() = withContext(dispatchers.io) { Shell.SU.available() }

}