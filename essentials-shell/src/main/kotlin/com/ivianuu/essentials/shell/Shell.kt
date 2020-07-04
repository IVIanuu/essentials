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

package com.ivianuu.essentials.shell

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Unscoped
import eu.chainfire.libsuperuser.Shell.SU
import kotlinx.coroutines.withContext

/**
 * Shell
 */
@Unscoped
class Shell(private val dispatchers: AppCoroutineDispatchers) {

    suspend fun run(vararg commands: String): List<String> = withContext(dispatchers.io) {
        SU.run(commands).toList()
    }

    suspend fun run(commands: Iterable<String>): List<String> =
        run(*commands.toList().toTypedArray())

    suspend fun isAvailable(): Boolean = withContext(dispatchers.io) {
        SU.available()
    }
}
