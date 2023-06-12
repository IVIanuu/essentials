/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Serializable

@Serializable data class NavBarPrefs(
  val hideNavBar: Boolean = false,
  val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT,
  val wasNavBarHidden: Boolean = false
) {
  companion object {
    @Provide fun initial(appConfig: AppConfig): @Initial NavBarPrefs = NavBarPrefs(
      hideNavBar = false,
      navBarRotationMode = if (appConfig.sdk >= 24) {
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
