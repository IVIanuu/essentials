package com.ivianuu.essentials.app

import android.app.Application
import android.content.Context
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.get

@Reader
val applicationContext: @ForApplication Context
    get() = get()
