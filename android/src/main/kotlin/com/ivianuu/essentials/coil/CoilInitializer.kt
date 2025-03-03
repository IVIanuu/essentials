/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coil

import coil.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import injekt.*

@Provide fun coilInitializer(imageLoaderFactory: () -> ImageLoader) =
  ScopeInitializer<AppScope> { Coil.setImageLoader(imageLoaderFactory) }
