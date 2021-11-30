/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import kotlin.coroutines.cancellation.CancellationException

actual fun Throwable.isFatal(): Boolean =
  this is CancellationException || this is ControlException || this is VirtualMachineError ||
      this is ThreadDeath || this is InterruptedException || this is LinkageError
