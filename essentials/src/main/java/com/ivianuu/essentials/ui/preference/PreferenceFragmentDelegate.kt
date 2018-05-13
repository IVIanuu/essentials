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

package com.ivianuu.essentials.ui.preference

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.support.v7.preference.PreferenceScreen
import android.support.v7.widget.RecyclerView

/**
 * Preference fragment delegate
 */
class PreferenceFragmentDelegate(
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
    private val tag: String = "prefs"
) {

    lateinit var fragment: PrefDelegateFragment

    var adapterFactory: AdapterFactory?
        get() = fragment.adapterFactory
        set(value) {
            fragment.adapterFactory = value
        }

    val preferenceManager: PreferenceManager
        get() = fragment.preferenceManager

    var preferenceScreen: PreferenceScreen?
        get() = fragment.preferenceScreen
        set(value) {
            fragment.preferenceScreen = value
        }

    fun onCreate() {
        var fragment = fragmentManager
            .findFragmentByTag(tag) as PrefDelegateFragment?

        if (fragment == null) {
            fragment = PrefDelegateFragment()
            fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .commitNow()
        }

        this.fragment = fragment
    }

    fun addPreferencesFromResource(res: Int) {
        fragment.addPreferencesFromResource(res)
    }

    fun setPreferencesFromResource(res: Int, key: CharSequence? = null) {
        fragment.setPreferencesFromResource(res, key.toString())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Preference> findPreference(key: CharSequence): T? =
        fragment.findPreference(key) as T

    fun <T : Preference> requirePreference(key: CharSequence): T =
            findPreference(key) ?: throw IllegalStateException("no preference found for $key")

    fun scrollToPreference(preference: Preference) {
        fragment.scrollToPreference(preference)
    }

    fun scrollToPreference(key: CharSequence) {
        fragment.scrollToPreference(key.toString())
    }

    fun setDivider(divider: Drawable?) {
        fragment.setDivider(divider)
    }

    fun setDividerHeight(height: Int) {
        fragment.setDividerHeight(height)
    }

    interface AdapterFactory {
        fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*>
    }
}

/**
 * Fragment for the [PreferenceFragmentDelegate]
 */
class PrefDelegateFragment : PreferenceFragmentCompat() {

    var adapterFactory: PreferenceFragmentDelegate.AdapterFactory? = DefaultAdapterFactory()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return adapterFactory?.onCreateAdapter(preferenceScreen) ?: super.onCreateAdapter(preferenceScreen)
    }

    private class DefaultAdapterFactory : PreferenceFragmentDelegate.AdapterFactory {
        override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
            return EnabledAwarePreferenceAdapter(preferenceScreen)
        }
    }
}