package com.ivianuu.essentials.ui.activity

import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.AndroidActivityContext
import com.ivianuu.injekt.given

@Reader
inline val androidActivityContext: AndroidActivityContext
    get() = given()
