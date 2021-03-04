package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Given

typealias NavBarFeatureSupported = Boolean

@Given
fun navBarFeatureSupported(
    @Given systemBuildInfo: SystemBuildInfo
): NavBarFeatureSupported = systemBuildInfo.sdk < 30
