package com.ivianuu.essentials.hidenavbar

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean

@Inject
@ApplicationScope
internal class NavBarPrefs(private val prefs: KPrefs) {
    val wasNavBarHidden = prefs.boolean("was_nav_bar_hidden")
}