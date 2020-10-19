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

package com.ivianuu.essentials.shell

import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.injekt.Binding
import eu.chainfire.libsuperuser.Shell.SU
import kotlinx.coroutines.withContext

typealias runShellCommand = suspend (String) -> List<String>
@Binding
fun runShellCommand(runShellCommands: runShellCommands): runShellCommand = { command ->
    runShellCommands(listOf(command))
}

typealias runShellCommands = suspend (List<String>) -> List<String>
@Binding
fun runShellCommands(ioDispatcher: IODispatcher): runShellCommands = { commands ->
    withContext(ioDispatcher) { SU.run(commands) }
}

typealias isShellAvailable = suspend () -> Boolean
@Binding
fun isShellAvailable(ioDispatcher: IODispatcher): isShellAvailable = {
    withContext(ioDispatcher) { SU.available() }

}
