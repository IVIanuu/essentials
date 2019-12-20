/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.theming

import androidx.ui.graphics.Color
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.boolean
import com.ivianuu.essentials.store.prefs.color
import com.ivianuu.essentials.store.prefs.enumString
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope

@ApplicationScope
@Single
class ThemePrefs(
    @DefaultTheme defaultTheme: Theme,
    factory: PrefBoxFactory
) {
    val primaryColor = factory.color("primary_color", defaultTheme.primaryColor)
    val secondaryColor = factory.color("secondary_color", defaultTheme.secondaryColor)
    val useBlack = factory.boolean("use_black", defaultTheme.useBlack)
    val twilightMode = factory.enumString(
        "twilight_mode",
        TwilightMode.System
    )
}