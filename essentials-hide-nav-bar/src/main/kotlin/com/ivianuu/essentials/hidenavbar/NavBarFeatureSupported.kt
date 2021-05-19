package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

typealias NavBarFeatureSupported = Boolean

@Provide fun navBarFeatureSupported(systemBuildInfo: SystemBuildInfo): NavBarFeatureSupported =
  systemBuildInfo.sdk < 30
