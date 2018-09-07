package com.ivianuu.essentials.ui.prefs

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.airbnb.epoxy.EpoxyController
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
import com.ivianuu.essentials.ui.simple.SimpleFragment
import com.ivianuu.essentials.util.ext.unsafeLazy

/**
 * Prefs fragment
 */
abstract class PrefsFragment : SimpleFragment() {

    protected open val sharedPreferencesName
        get() =
            EpoxyPrefsPlugins.getDefaultSharedPreferencesName(requireContext())

    protected val sharedPreferences: SharedPreferences by unsafeLazy {
        requireContext().getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    }

    protected open val usePreferenceDividerDecoration = true

    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            postInvalidate()
    }

    override fun onStart() {
        super.onStart()
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override fun onStop() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (usePreferenceDividerDecoration) {
            optionalRecyclerView?.addItemDecoration(PreferenceDividerDecoration(requireContext()))
        }
    }

    protected fun EpoxyController.preference(init: PreferenceModel.Builder.() -> Unit) =
        preference(requireContext(), init)

    protected fun EpoxyController.categoryPreference(init: CategoryPreferenceModel.Builder.() -> Unit) =
        categoryPreference(requireContext(), init)

    protected fun EpoxyController.checkboxPreference(init: CheckboxPreferenceModel.Builder.() -> Unit) =
        checkboxPreference(requireContext(), init)

    protected fun EpoxyController.editTextPreference(init: EditTextPreferenceModel.Builder.() -> Unit) =
        editTextPreference(requireContext(), init)

    protected fun EpoxyController.multiSelectListPreference(init: MultiSelectListPreferenceModel.Builder.() -> Unit) =
        multiSelectListPreference(requireContext(), init)

    protected fun EpoxyController.radioButtonPreference(init: RadioButtonPreferenceModel.Builder.() -> Unit) =
        radioButtonPreference(requireContext(), init)

    protected fun EpoxyController.seekBarPreference(init: SeekBarPreferenceModel.Builder.() -> Unit) =
        seekBarPreference(requireContext(), init)

    protected fun EpoxyController.singleItemListPreference(init: SingleItemListPreferenceModel.Builder.() -> Unit) =
        singleItemListPreference(requireContext(), init)

    protected fun EpoxyController.switchPreference(init: SwitchPreferenceModel.Builder.() -> Unit) =
        switchPreference(requireContext(), init)
}