/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.recentapps

import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@JvmInline value class CurrentAppProvider(val currentApp: Flow<String?>)

context(RecentAppsProvider) @Provide fun currentAppProvider() = CurrentAppProvider(
  recentApps
    .map { it.firstOrNull() }
    .distinctUntilChanged()
)
