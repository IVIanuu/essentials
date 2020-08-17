package com.ivianuu.essentials.app

import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.AndroidApplicationContext
import com.ivianuu.injekt.given

@Reader
inline val androidApplicationContext: AndroidApplicationContext
    get() = given()
