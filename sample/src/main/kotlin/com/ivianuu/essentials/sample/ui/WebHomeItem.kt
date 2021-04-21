package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.web.ui.*
import com.ivianuu.injekt.*

@Given
val webHomeItem = HomeItem("Web") {
    WebKey(title = "Google", "https://google.com")
}
