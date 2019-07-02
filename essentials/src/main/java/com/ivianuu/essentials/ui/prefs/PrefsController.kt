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

package com.ivianuu.essentials.ui.prefs

import android.os.Bundle
import android.view.View
import com.ivianuu.epoxyprefs.*
import com.ivianuu.essentials.ui.simple.ListController
import com.ivianuu.essentials.ui.traveler.ControllerNavOptions
import com.ivianuu.essentials.ui.traveler.key.UrlKey
import com.ivianuu.traveler.push


/**
 * Prefs controller
 */
abstract class PrefsController : ListController() {

    open val preferenceContext by lazy {
        EpoxyPrefsPlugins.getDefaultContext(activity)
    }

    protected open val usePreferenceDividerDecoration = true

    private val changeListener: (String) -> Unit = { postInvalidate() }

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)
        if (usePreferenceDividerDecoration) {
            recyclerView.addItemDecoration(PreferenceDividerDecoration(activity))
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
        keyProvider: () -> Any
    ) {
        onClick {
            travelerRouter.push(keyProvider())
            return@onClick true
        }
    }

    protected fun AbstractPreferenceModel.Builder<*>.navigateOnClickWithOptions(
        provider: () -> Pair<Any, ControllerNavOptions>
    ) {
        onClick {
            val (key, options) = provider()
            travelerRouter.push(key, options)
            return@onClick true
        }
    }

    protected fun AbstractPreferenceModel.Builder<*>.openUrlOnClick(
        urlProvider: () -> String
    ) {
        navigateOnClick { UrlKey(urlProvider()) }
    }

}
