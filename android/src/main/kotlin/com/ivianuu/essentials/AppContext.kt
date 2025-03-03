/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.app.*
import android.content.*
import injekt.*

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class AppContextTag {
  @Provide companion object {
    @Provide fun appContext(app: Application): AppContext = app
  }
}

typealias AppContext = @AppContextTag Context
