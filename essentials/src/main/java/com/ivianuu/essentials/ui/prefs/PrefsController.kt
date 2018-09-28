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

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.director.requireActivity
import com.ivianuu.epoxyprefs.CategoryPreferenceModel
import com.ivianuu.epoxyprefs.CheckboxPreferenceModel
import com.ivianuu.epoxyprefs.EditTextPreferenceModel
import com.ivianuu.epoxyprefs.EpoxyPrefsPlugins
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
import com.ivianuu.essentials.util.ext.unsafeLazy

/**
 * Prefs controller
 */
abstract class PrefsController : SimpleController() {

    protected open val sharedPreferencesName
        get() =
            EpoxyPrefsPlugins.getDefaultSharedPreferencesName(requireActivity())

    protected val sharedPreferences: SharedPreferences by unsafeLazy {
        requireActivity().getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    }

    protected open val usePreferenceDividerDecoration = true

    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            postInvalidate()
        }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        if (usePreferenceDividerDecoration) {
            optionalRecyclerView?.addItemDecoration(PreferenceDividerDecoration(requireActivity()))
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override fun onDestroyView(view: View) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        super.onDestroyView(view)
    }

    protected fun EpoxyController.preference(init: PreferenceModel.Builder.() -> Unit) =
        preference(requireActivity(), init)

    protected fun EpoxyController.categoryPreference(init: CategoryPreferenceModel.Builder.() -> Unit) =
        categoryPreference(requireActivity(), init)

    protected fun EpoxyController.checkboxPreference(init: CheckboxPreferenceModel.Builder.() -> Unit) =
        checkboxPreference(requireActivity(), init)

    protected fun EpoxyController.editTextPreference(init: EditTextPreferenceModel.Builder.() -> Unit) =
        editTextPreference(requireActivity(), init)

    protected fun EpoxyController.multiSelectListPreference(init: MultiSelectListPreferenceModel.Builder.() -> Unit) =
        multiSelectListPreference(requireActivity(), init)

    protected fun EpoxyController.radioButtonPreference(init: RadioButtonPreferenceModel.Builder.() -> Unit) =
        radioButtonPreference(requireActivity(), init)

    protected fun EpoxyController.seekBarPreference(init: SeekBarPreferenceModel.Builder.() -> Unit) =
        seekBarPreference(requireActivity(), init)

    protected fun EpoxyController.singleItemListPreference(init: SingleItemListPreferenceModel.Builder.() -> Unit) =
        singleItemListPreference(requireActivity(), init)

    protected fun EpoxyController.switchPreference(init: SwitchPreferenceModel.Builder.() -> Unit) =
        switchPreference(requireActivity(), init)
}