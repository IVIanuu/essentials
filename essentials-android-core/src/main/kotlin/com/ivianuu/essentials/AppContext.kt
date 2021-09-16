package com.ivianuu.essentials

import android.app.Application
import android.content.Context
import com.ivianuu.injekt.Provide

typealias AppContext = Context

@Provide fun appContext(app: Application): AppContext = app
