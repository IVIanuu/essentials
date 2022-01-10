/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shell

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import eu.chainfire.libsuperuser.Shell.*
import kotlinx.coroutines.*

interface Shell {
  suspend fun isAvailable(): Boolean

  suspend fun run(vararg commands: String): Result<List<String>, Throwable>
}

@Provide class ShellImpl(private val context: IOContext) : Shell {
  override suspend fun isAvailable() = withContext(context) {
    catch { SU.available() }.getOrElse { false }
  }

  override suspend fun run(vararg commands: String) = withContext(context) {
    catch { SU.run(commands)!! }
  }
}
