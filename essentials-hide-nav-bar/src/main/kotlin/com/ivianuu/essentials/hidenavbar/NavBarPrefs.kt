/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Serializable

@Serializable @Optics data class NavBarPrefs(
  val hideNavBar: Boolean = false,
  val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT,
  val wasNavBarHidden: Boolean = false
) {
  companion object {
    context(SystemBuildInfo) @Provide fun initial(): @Initial NavBarPrefs = NavBarPrefs(
      hideNavBar = false,
      navBarRotationMode = if (systemSdk >= 24) {
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
