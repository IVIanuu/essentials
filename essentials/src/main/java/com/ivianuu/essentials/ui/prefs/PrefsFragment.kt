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
import com.ivianuu.epoxyprefs.EpoxyPrefsPlugins
import com.ivianuu.epoxyprefs.PreferenceDividerDecoration
import com.ivianuu.epoxyprefs.PreferenceEpoxyController
import com.ivianuu.epoxyprefs.preferenceEpoxyController
import com.ivianuu.essentials.ui.simple.ListFragment

/**
 * Prefs fragment
 */
abstract class PrefsFragment : ListFragment() {

    open val preferenceContext by lazy {
        EpoxyPrefsPlugins.getDefaultContext(requireContext())
    }

    protected open val usePreferenceDividerDecoration = true

    private val changeListener: (String) -> Unit = { postInvalidate() }

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)
        if (usePreferenceDividerDecoration) {
            recyclerView.addItemDecoration(PreferenceDividerDecoration(requireContext()))
        }
    }

    override fun onStart() {
        super.onStart()
        preferenceContext.addChangeListener(changeListener)
    }

    override fun onStop() {
        super.onStop()
        preferenceContext.removeChangeListener(changeListener)
    }

    protected fun epoxyController(buildModels: PreferenceEpoxyController.() -> Unit): PreferenceEpoxyController =
        preferenceEpoxyController(preferenceContext, buildModels)

}
