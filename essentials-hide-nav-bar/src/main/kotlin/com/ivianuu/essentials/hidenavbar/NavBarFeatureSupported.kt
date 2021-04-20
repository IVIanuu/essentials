package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

typealias NavBarFeatureSupported = Boolean

@Given
fun navBarFeatureSupported(
    @Given systemBuildInfo: SystemBuildInfo
): NavBarFeatureSupported = systemBuildInfo.sdk < 30
