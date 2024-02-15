/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shell

import arrow.core.Either
import arrow.core.getOrElse
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.injekt.Provide
import eu.chainfire.libsuperuser.Shell.SU
import kotlinx.coroutines.withContext

interface Shell {
  suspend fun isAvailable(): Boolean

  suspend fun run(vararg commands: String): Either<Throwable, List<String>>
}

@Provide class ShellImpl(private val coroutineContexts: CoroutineContexts) : Shell {
  override suspend fun isAvailable() = withContext(coroutineContexts.io) {
    Either.catch { SU.available() }.getOrElse { false }
  }

  override suspend fun run(vararg commands: String) = withContext(coroutineContexts.io) {
    Either.catch { SU.run(commands)!! }
  }
}
