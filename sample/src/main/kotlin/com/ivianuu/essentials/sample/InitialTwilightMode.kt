package com.ivianuu.essentials.sample

import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.injekt.Given

@Given
val initialTwilightPrefs: @Initial TwilightPrefs get() = TwilightPrefs(
    TwilightMode.DARK,
    true
)
