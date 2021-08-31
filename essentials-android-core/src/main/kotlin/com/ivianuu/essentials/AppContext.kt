package com.ivianuu.essentials

import android.app.*
import android.content.*
import com.ivianuu.injekt.*

typealias AppContext = Context

@Provide fun appContext(app: Application): AppContext = app
