package com.ivianuu.essentials.app

import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.given

@Reader
inline val applicationContext: ApplicationContext
    get() = given()
