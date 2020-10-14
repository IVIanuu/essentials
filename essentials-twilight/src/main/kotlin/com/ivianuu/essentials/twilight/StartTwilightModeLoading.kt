package com.ivianuu.essentials.twilight

import com.ivianuu.essentials.app.AppInitializerBinding
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AppInitializerBinding
@FunBinding
fun startTwilightModeLoading(
    globalScope: GlobalScope,
    twilightStateFlow: TwilightStateFlow,
) {
    globalScope.launch {
        twilightStateFlow.first()
    }
}
