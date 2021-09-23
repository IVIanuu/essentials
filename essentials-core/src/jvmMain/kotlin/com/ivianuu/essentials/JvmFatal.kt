package com.ivianuu.essentials

import kotlin.coroutines.cancellation.CancellationException

actual fun Throwable.isFatal(): Boolean =
  this is CancellationException || this is ControlException || this is VirtualMachineError ||
      this is ThreadDeath || this is InterruptedException || this is LinkageError
