package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.web.ui.*
import com.ivianuu.injekt.*

@Provide val webHomeItem = HomeItem("Web") { WebKey(title = "Google", "https://google.com") }
