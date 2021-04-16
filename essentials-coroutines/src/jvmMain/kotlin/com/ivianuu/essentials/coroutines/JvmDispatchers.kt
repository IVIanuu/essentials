package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Given
actual inline val ioDispatcher: IODispatcher
    get() = Dispatchers.IO
