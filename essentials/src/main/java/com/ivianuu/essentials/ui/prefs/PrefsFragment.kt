package com.ivianuu.essentials.ui.prefs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.epoxyprefs.*
import com.ivianuu.essentials.ui.simple.SimpleFragment
import javax.inject.Inject

/**
 * Prefs fragment
 */
abstract class PrefsFragment : SimpleFragment() {

    @Inject lateinit var sharedPreferences: SharedPreferences

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