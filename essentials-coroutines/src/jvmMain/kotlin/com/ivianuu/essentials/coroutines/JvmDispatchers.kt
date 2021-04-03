package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Given
import kotlinx.coroutines.Dispatchers

@Given
actual inline val ioDispatcher: IODispatcher
    get() = Dispatchers.IO
