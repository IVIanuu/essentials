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

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.ivianuu.director.activity
import com.ivianuu.essentials.ui.simple.SimpleController
import com.ivianuu.injekt.inject
import com.ivianuu.list.ModelController
import com.ivianuu.list.addTo
import com.ivianuu.listprefs.CategoryPreferenceModel
import com.ivianuu.listprefs.CheckboxPreferenceModel
import com.ivianuu.listprefs.EditTextPreferenceModel
import com.ivianuu.listprefs.MultiSelectListPreferenceModel
import com.ivianuu.listprefs.PreferenceDividerDecoration
import com.ivianuu.listprefs.PreferenceModel
import com.ivianuu.listprefs.RadioButtonPreferenceModel
import com.ivianuu.listprefs.SeekBarPreferenceModel
import com.ivianuu.listprefs.SingleItemListPreferenceModel
import com.ivianuu.listprefs.SwitchPreferenceModel
import com.ivianuu.listprefs.context

/**
 * Prefs controller
 */
abstract class PrefsController : SimpleController() {

    open val sharedPreferences by inject<SharedPreferences>()

    protected open val usePreferenceDividerDecoration = true

    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            postInvalidate()
        }

    override fun onBindView(view: View, savedViewState: Bundle?) {
        super.onBindView(view, savedViewState)
        if (usePreferenceDividerDecoration) {
            optionalRecyclerView?.addItemDecoration(PreferenceDividerDecoration(activity))
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    protected fun ModelController.preference(init: PreferenceModel.() -> Unit): PreferenceModel =
        PreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.categoryPreference(init: CategoryPreferenceModel.() -> Unit): CategoryPreferenceModel =
        CategoryPreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.checkboxPreference(init: CheckboxPreferenceModel.() -> Unit): CheckboxPreferenceModel =
        CheckboxPreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.editTextPreference(init: EditTextPreferenceModel.() -> Unit): EditTextPreferenceModel =
        EditTextPreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.multiSelectListPreference(init: MultiSelectListPreferenceModel.() -> Unit): MultiSelectListPreferenceModel =
        MultiSelectListPreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.radioButtonPreference(init: RadioButtonPreferenceModel.() -> Unit): RadioButtonPreferenceModel =
        RadioButtonPreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.seekBarPreference(init: SeekBarPreferenceModel.() -> Unit): SeekBarPreferenceModel =
        SeekBarPreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.singleItemListPreference(init: SingleItemListPreferenceModel.() -> Unit): SingleItemListPreferenceModel =
        SingleItemListPreferenceModel().apply { context(activity) }.apply(init).addTo(this)

    protected fun ModelController.switchPreference(init: SwitchPreferenceModel.() -> Unit): SwitchPreferenceModel =
        SwitchPreferenceModel().apply { context(activity) }.apply(init).addTo(this)
}
