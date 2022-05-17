/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.android.prefs.DataStoreModule
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.injekt.Provide
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable @Optics data class NavBarPrefs(
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
    ): DataStoreModule<NavBarPrefs> = DataStoreModule("nav_bar_prefs", initialFactory)
  }
}
