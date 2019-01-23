/*
 * Copyright 2018 Manuel Wrage
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

import android.os.Build
import com.ivianuu.essentials.util.enumString
import com.ivianuu.injekt.android.APPLICATION_SCOPE
import com.ivianuu.injekt.annotations.Name
import com.ivianuu.injekt.annotations.Single
import com.ivianuu.kprefs.KPrefs

/**
 * Nav bar prefs
 */
@Single(scopeName = APPLICATION_SCOPE)
class NavBarPrefs(@Name(NAV_BAR_PREFS) prefs: KPrefs) {
    val manageNavBar = prefs.boolean("manage_nav_bar")
    val fullOverscan = prefs.boolean("full_overscan")
    val navBarHidden = prefs.boolean("nav_bar_hidden")
    val rotationMode = prefs.enumString(
        "rotation_mode",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NavBarRotationMode.NOUGAT
        } else {
            NavBarRotationMode.MARSHMALLOW
        }
    )
    val showNavBarScreenOff = prefs.boolean("show_nav_bar_screen_off", true)

    internal val wasNavBarHidden = prefs.boolean("was_nav_bar_hidden")
}