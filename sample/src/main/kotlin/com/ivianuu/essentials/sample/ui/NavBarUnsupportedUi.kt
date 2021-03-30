package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.hidenavbar.ui.NavBarUnsupportedKey
import com.ivianuu.injekt.Given

@Given
val navBarUnsupportedHomeItem = HomeItem("Nav bar unsupported") { NavBarUnsupportedKey() }
