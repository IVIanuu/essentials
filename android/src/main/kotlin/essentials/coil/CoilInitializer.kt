/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.coil

import coil.*
import essentials.*
import essentials.app.*
import injekt.*

@Provide fun coilInitializer(imageLoaderFactory: () -> ImageLoader) =
  ScopeInitializer<AppScope> { Coil.setImageLoader(imageLoaderFactory) }
