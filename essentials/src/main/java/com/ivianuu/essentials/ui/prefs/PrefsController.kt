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
import com.ivianuu.list.ItemController
import com.ivianuu.list.addTo
import com.ivianuu.listprefs.*

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

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)
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

    // todo find a better way for those extensions

    protected fun ItemController.PreferenceItem(init: PreferenceItem.() -> Unit): PreferenceItem =
        PreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.CategoryPreferenceItem(init: CategoryPreferenceItem.() -> Unit): CategoryPreferenceItem =
        CategoryPreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.CheckboxPreferenceItem(init: CheckboxPreferenceItem.() -> Unit): CheckboxPreferenceItem =
        CheckboxPreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.EditTextPreferenceItem(init: EditTextPreferenceItem.() -> Unit): EditTextPreferenceItem =
        EditTextPreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.MultiSelectListPreferenceItem(init: MultiSelectListPreferenceItem.() -> Unit): MultiSelectListPreferenceItem =
        MultiSelectListPreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.RadioButtonPreferenceItem(init: RadioButtonPreferenceItem.() -> Unit): RadioButtonPreferenceItem =
        RadioButtonPreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.SeekBarPreferenceItem(init: SeekBarPreferenceItem.() -> Unit): SeekBarPreferenceItem =
        SeekBarPreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.SingleItemListPreferenceItem(init: SingleItemListPreferenceItem.() -> Unit): SingleItemListPreferenceItem =
        SingleItemListPreferenceItem().apply { context = activity }.apply(init).addTo(this)

    protected fun ItemController.SwitchPreferenceItem(init: SwitchPreferenceItem.() -> Unit): SwitchPreferenceItem =
        SwitchPreferenceItem().apply { context = activity }.apply(init).addTo(this)
}
