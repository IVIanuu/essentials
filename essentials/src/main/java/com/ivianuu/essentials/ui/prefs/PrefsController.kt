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
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.director.activity
import com.ivianuu.director.context
import com.ivianuu.epoxyprefs.*
import com.ivianuu.essentials.ui.simple.ListController

/**
 * Prefs controller
 */
abstract class PrefsController : ListController() {

    open val preferenceContext by lazy {
        EpoxyPrefsPlugins.getDefaultContext(context)
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

    fun EpoxyController.Preference(block: PreferenceModel.Builder.() -> Unit): PreferenceModel =
        PreferenceModel.Builder().injectContext().apply(block).build().also { it.addTo(this) }

    fun EpoxyController.CategoryPreference(block: CategoryPreferenceModel.Builder.() -> Unit): CategoryPreferenceModel =
        CategoryPreferenceModel.Builder().injectContext().apply(block).build().also { it.addTo(this) }

    fun EpoxyController.CheckboxPreference(block: CheckboxPreferenceModel.Builder.() -> Unit): CheckboxPreferenceModel =
        CheckboxPreferenceModel.Builder().injectContext().apply(block).build().also { it.addTo(this) }

    fun EpoxyController.EditTextPreference(block: EditTextPreferenceModel.Builder.() -> Unit): EditTextPreferenceModel =
        EditTextPreferenceModel.Builder().injectContext().apply(block).build().also { it.addTo(this) }

    fun EpoxyController.MultiSelectListPreference(block: MultiSelectListPreferenceModel.Builder.() -> Unit): MultiSelectListPreferenceModel =
        MultiSelectListPreferenceModel.Builder().injectContext().apply(block).build().also {
            it.addTo(
                this
            )
        }

    fun EpoxyController.RadioButtonPreference(block: RadioButtonPreferenceModel.Builder.() -> Unit): RadioButtonPreferenceModel =
        RadioButtonPreferenceModel.Builder().injectContext().apply(block).build().also {
            it.addTo(
                this
            )
        }

    fun EpoxyController.SeekBarPreference(block: SeekBarPreferenceModel.Builder.() -> Unit): SeekBarPreferenceModel =
        SeekBarPreferenceModel.Builder().injectContext().apply(block).build().also { it.addTo(this) }

    fun EpoxyController.SingleItemListPreference(block: SingleItemListPreferenceModel.Builder.() -> Unit): SingleItemListPreferenceModel =
        SingleItemListPreferenceModel.Builder().injectContext().apply(block).build().also {
            it.addTo(
                this
            )
        }

    fun EpoxyController.SwitchPreference(block: SwitchPreferenceModel.Builder.() -> Unit): SwitchPreferenceModel =
        SwitchPreferenceModel.Builder().injectContext().apply(block).build().also { it.addTo(this) }

    private fun <T : AbstractPreferenceModel.Builder<*>> T.injectContext() = apply {
        context(preferenceContext)
    }
}
