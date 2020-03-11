package com.ivianuu.essentials.app

import com.ivianuu.injekt.Scope
import com.ivianuu.injekt.ScopeMarker

// todo this should be built into injekt
@ScopeMarker
annotation class ApplicationScope {
    companion object : Scope
}
