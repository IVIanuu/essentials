/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

@JvmInline value class NavBarFeatureSupported(val value: Boolean)

@Provide fun navBarFeatureSupported(systemBuildInfo: SystemBuildInfo) =
  NavBarFeatureSupported(systemBuildInfo.sdk < 30)
