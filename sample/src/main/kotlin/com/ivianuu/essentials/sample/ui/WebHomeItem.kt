/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.web.ui.WebScreen
import com.ivianuu.injekt.Provide

@Provide val webHomeItem = HomeItem("Web") { WebScreen(title = "Google", "https://google.com") }
