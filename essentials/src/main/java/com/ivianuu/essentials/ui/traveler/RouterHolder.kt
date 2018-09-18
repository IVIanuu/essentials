package com.ivianuu.essentials.ui.traveler

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.ivianuu.epoxyprefs.PreferenceModel
import com.ivianuu.essentials.util.ext.goBackOnNavigationClick
import com.ivianuu.essentials.util.ext.navigateOnClick
import com.ivianuu.traveler.Router

/**
 * Router aware component
 */
interface RouterHolder {
    val router: Router

    fun Toolbar.goBackOnNavigationClick() {
        goBackOnNavigationClick(router)
    }

    fun View.navigateOnClick(key: () -> Any) {
        navigateOnClick(router, key)
    }

    fun PreferenceModel.Builder.navigateOnClick(key: () -> Any) {
        navigateOnClick(router, key)
    }
}