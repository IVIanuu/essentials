/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.hidenavbar

import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.injekt.Provide
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
