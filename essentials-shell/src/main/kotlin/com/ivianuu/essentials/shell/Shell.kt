/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shell

import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrElse
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import eu.chainfire.libsuperuser.Shell.SU
import kotlinx.coroutines.withContext

interface Shell {
  suspend fun isAvailable(): Boolean

  suspend fun run(vararg commands: String): Result<List<String>, Throwable>
}

@Provide class ShellImpl(private val dispatcher: IODispatcher) : Shell {
  override suspend fun isAvailable() = withContext(dispatcher) {
    catch { SU.available() }.getOrElse { false }
  }

  override suspend fun run(vararg commands: String) = withContext(dispatcher) {
    catch { SU.run(commands)!! }
  }
}
