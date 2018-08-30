package com.ivianuu.essentials.sample.ui

import android.os.Bundle
import android.view.View
import com.ivianuu.essentials.util.ext.epoxyController

/**
 * @author Manuel Wrage (IVIanuu)
 */
class PrefsFragment : com.ivianuu.essentials.ui.prefs.PrefsFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "Prefs"
    }

    override fun epoxyController() = epoxyController {
        switchPreference {
            key("switch")
            summary("Summary.")
            title("Title")
        }
    }

}