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

import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import eu.chainfire.libsuperuser.Shell.*
import kotlinx.coroutines.*

typealias IsShellAvailableUseCase = suspend () -> Boolean

@Given fun isShellAvailableUseCase(@Given dispatcher: IODispatcher): IsShellAvailableUseCase = {
  withContext(dispatcher) {
    catch { SU.available() }.getOrElse { false }
  }
}

typealias RunShellCommandUseCase = suspend (List<String>) -> Result<List<String>, Throwable>

@Given
fun runShellCommandUseCase(@Given dispatcher: IODispatcher): RunShellCommandUseCase = { commands ->
  withContext(dispatcher) { catch { SU.run(commands) !! } }
}
