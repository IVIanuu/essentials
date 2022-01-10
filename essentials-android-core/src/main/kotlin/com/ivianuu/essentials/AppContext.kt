/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.app.*
import android.content.*
import com.ivianuu.injekt.*

@Tag annotation class AppContextTag
typealias AppContext = @AppContextTag Context

@Provide fun appContext(app: Application): AppContext = app
