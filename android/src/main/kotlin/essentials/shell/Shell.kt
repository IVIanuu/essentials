/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.shell

import com.github.michaelbull.result.*
import essentials.*
import essentials.coroutines.*
import eu.chainfire.libsuperuser.Shell.*
import injekt.*
import kotlinx.coroutines.*

@Provide class Shell(private val coroutineContexts: CoroutineContexts) {
  suspend fun isAvailable() = withContext(coroutineContexts.io) {
    catch { SU.available() }.getOrElse { false }
  }

  suspend fun run(vararg commands: String) = withContext(coroutineContexts.io) {
    catch { SU.run(commands)!! }
  }
}
