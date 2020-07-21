package com.ivianuu.essentials.ui.activity

import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ActivityContext
import com.ivianuu.injekt.given

@Reader
inline val activityContext: ActivityContext
    get() = given()
