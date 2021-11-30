/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.hidenavbar.ui.NavBarUnsupportedKey
import com.ivianuu.injekt.Provide

@Provide val navBarUnsupportedHomeItem = HomeItem("Nav bar unsupported") { NavBarUnsupportedKey }
