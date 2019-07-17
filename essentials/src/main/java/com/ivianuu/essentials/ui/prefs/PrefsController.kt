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

package com.ivianuu.essentials.ui.prefs

import android.view.View
import com.ivianuu.director.requireActivity
import com.ivianuu.epoxyprefs.AbstractPreferenceModel
import com.ivianuu.epoxyprefs.EpoxyPrefsPlugins
import com.ivianuu.epoxyprefs.PreferenceDividerDecoration
import com.ivianuu.epoxyprefs.PreferenceEpoxyController
import com.ivianuu.epoxyprefs.preferenceEpoxyController
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.simple.ListController

/**
 * Prefs controller
 */
abstract class PrefsController : ListController() {

    open val preferenceContext by lazy {
        EpoxyPrefsPlugins.getDefaultContext(requireActivity().applicationContext)
    }

    protected open val usePreferenceDividerDecoration = true

    private val changeListener: (String) -> Unit = { postInvalidate() }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        if (usePreferenceDividerDecoration) {
            recyclerView.addItemDecoration(PreferenceDividerDecoration(requireActivity()))
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        preferenceContext.addChangeListener(changeListener)
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        preferenceContext.removeChangeListener(changeListener)
    }

    protected fun epoxyController(buildModels: PreferenceEpoxyController.() -> Unit): PreferenceEpoxyController =
        preferenceEpoxyController(preferenceContext, buildModels)

    protected fun AbstractPreferenceModel.Builder<*>.navigateOnClick(
        routeProvider: () -> ControllerRoute
    ) {
        onClick {
            navigator.push(routeProvider())
            return@onClick true
        }
    }

    protected fun AbstractPreferenceModel.Builder<*>.openUrlOnClick(
        urlProvider: () -> String
    ) {
        error("lol")
        // todo navigateOnClick { UrlKey(urlProvider()) }
    }

}
