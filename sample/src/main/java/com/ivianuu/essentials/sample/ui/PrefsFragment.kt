package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.util.ext.epoxyController

/**
 * @author Manuel Wrage (IVIanuu)
 */
class PrefsFragment : com.ivianuu.essentials.ui.prefs.PrefsFragment() {

    override val toolbarTitle = "Prefs"

    override fun epoxyController() = epoxyController {
        switchPreference {
            key("switch")
            summary("Summary.")
            title("Title")
        }
    }

}