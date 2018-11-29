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
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.epoxyprefs.CategoryPreferenceModel
import com.ivianuu.epoxyprefs.CheckboxPreferenceModel
import com.ivianuu.epoxyprefs.EditTextPreferenceModel
import com.ivianuu.epoxyprefs.MultiSelectListPreferenceModel
import com.ivianuu.epoxyprefs.PreferenceDividerDecoration
import com.ivianuu.epoxyprefs.PreferenceModel
import com.ivianuu.epoxyprefs.RadioButtonPreferenceModel
import com.ivianuu.epoxyprefs.SeekBarPreferenceModel
import com.ivianuu.epoxyprefs.SingleItemListPreferenceModel
import com.ivianuu.epoxyprefs.SwitchPreferenceModel
import com.ivianuu.epoxyprefs.categoryPreference
import com.ivianuu.epoxyprefs.checkboxPreference
import com.ivianuu.epoxyprefs.editTextPreference
import com.ivianuu.epoxyprefs.multiSelectListPreference
import com.ivianuu.epoxyprefs.preference
import com.ivianuu.epoxyprefs.radioButtonPreference
import com.ivianuu.epoxyprefs.seekBarPreference
import com.ivianuu.epoxyprefs.singleItemListPreference
import com.ivianuu.epoxyprefs.switchPreference
import com.ivianuu.essentials.ui.simple.SimpleController
import javax.inject.Inject

/**
 * Prefs controller
 */
abstract class PrefsController : SimpleController() {

    @Inject lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override fun onUnbindView(view: View) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        super.onUnbindView(view)
    }

    protected fun EpoxyController.preference(init: PreferenceModel.Builder.() -> Unit) =
        preference(activity, init)

    protected fun EpoxyController.categoryPreference(init: CategoryPreferenceModel.Builder.() -> Unit) =
        categoryPreference(activity, init)

    protected fun EpoxyController.checkboxPreference(init: CheckboxPreferenceModel.Builder.() -> Unit) =
        checkboxPreference(activity, init)

    protected fun EpoxyController.editTextPreference(init: EditTextPreferenceModel.Builder.() -> Unit) =
        editTextPreference(activity, init)

    protected fun EpoxyController.multiSelectListPreference(init: MultiSelectListPreferenceModel.Builder.() -> Unit) =
        multiSelectListPreference(activity, init)

    protected fun EpoxyController.radioButtonPreference(init: RadioButtonPreferenceModel.Builder.() -> Unit) =
        radioButtonPreference(activity, init)

    protected fun EpoxyController.seekBarPreference(init: SeekBarPreferenceModel.Builder.() -> Unit) =
        seekBarPreference(activity, init)

    protected fun EpoxyController.singleItemListPreference(init: SingleItemListPreferenceModel.Builder.() -> Unit) =
        singleItemListPreference(activity, init)

    protected fun EpoxyController.switchPreference(init: SwitchPreferenceModel.Builder.() -> Unit) =
        switchPreference(activity, init)
}
