/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.injekt.*
import kotlinx.serialization.*

@Serializable data class NavBarPrefs(
  @SerialName("hide_nav_bar") val hideNavBar: Boolean = false,
  @SerialName("nav_bar_rotation_mode") val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT,
  @SerialName("was_nav_bar_hidden") val wasNavBarHidden: Boolean = false
) {
  companion object {
    @Provide fun initial(systemBuildInfo: SystemBuildInfo): @Initial NavBarPrefs = NavBarPrefs(
      hideNavBar = false,
      navBarRotationMode = if (systemBuildInfo.sdk >= 24) {
        NavBarRotationMode.NOUGAT
      } else {
        NavBarRotationMode.MARSHMALLOW
      }
    )

    @Provide fun prefModule(
      initialFactory: () -> @Initial NavBarPrefs
    ): PrefModule<NavBarPrefs> = PrefModule(initialFactory)
  }
}
