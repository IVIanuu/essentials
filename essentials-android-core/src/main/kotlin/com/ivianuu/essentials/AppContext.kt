/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.app.Application
import android.content.Context
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

@Tag annotation class AppContextTag
typealias AppContext = @AppContextTag Context

@Provide fun appContext(app: Application): AppContext = app
