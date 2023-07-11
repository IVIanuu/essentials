/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coil

import coil.Coil
import coil.ImageLoader
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.ScopeInitializer
import com.ivianuu.injekt.Provide

fun interface CoilInitializer : ScopeInitializer<AppScope>

@Provide fun coilInitializer(imageLoaderFactory: () -> ImageLoader) =
  CoilInitializer { Coil.setImageLoader(imageLoaderFactory) }
