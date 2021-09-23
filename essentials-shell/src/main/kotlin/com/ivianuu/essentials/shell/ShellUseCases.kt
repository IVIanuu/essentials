/*
 * Copyright 2021 Manuel Wrage
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

import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrElse
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import eu.chainfire.libsuperuser.Shell.SU
import kotlinx.coroutines.withContext

typealias IsShellAvailableUseCase = suspend () -> Boolean

@Provide fun isShellAvailableUseCase(dispatcher: IODispatcher): IsShellAvailableUseCase = {
  withContext(dispatcher) {
    catch { SU.available() }.getOrElse { false }
  }
}

typealias RunShellCommandUseCase = suspend (List<String>) -> Result<List<String>, Throwable>

@Provide
fun runShellCommandUseCase(dispatcher: IODispatcher): RunShellCommandUseCase = { commands ->
  withContext(dispatcher) { catch { SU.run(commands)!! } }
}
