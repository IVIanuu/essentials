package com.ivianuu.essentials.ui.activity

import android.content.Context
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ForActivity
import com.ivianuu.injekt.get

@Reader
val activityContext: @ForActivity Context
    get() = get()
