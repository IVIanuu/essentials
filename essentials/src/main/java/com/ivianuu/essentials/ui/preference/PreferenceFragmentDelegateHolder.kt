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
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceManager
import android.support.v7.preference.PreferenceScreen

/**
 * Holds a [PreferenceFragmentDelegate]
 */
interface PreferenceFragmentDelegateHolder {
    val preferenceFragmentDelegate: PreferenceFragmentDelegate

    var adapterFactory: PreferenceFragmentDelegate.AdapterFactory?
        get() = preferenceFragmentDelegate.adapterFactory
        set(value) {
            preferenceFragmentDelegate.adapterFactory = value
        }

    val preferenceManager: PreferenceManager?
        get() = preferenceFragmentDelegate.preferenceManager

    var preferenceScreen: PreferenceScreen?
        get() = preferenceFragmentDelegate.preferenceScreen
        set(value) {
            preferenceFragmentDelegate.preferenceScreen = value
        }

    fun addPreferencesFromResource(res: Int) {
        preferenceFragmentDelegate.addPreferencesFromResource(res)
    }

    fun setPreferencesFromResource(res: Int, key: CharSequence? = null) {
        preferenceFragmentDelegate.setPreferencesFromResource(res, key.toString())
    }

    fun <T : Preference> findPreference(key: CharSequence): T? =
        preferenceFragmentDelegate.findPreference(key)

    fun <T : Preference> requirePreference(key: CharSequence): T =
        findPreference(key) ?: throw IllegalStateException("no preference found for $key")

    fun scrollToPreference(preference: Preference) {
        preferenceFragmentDelegate.scrollToPreference(preference)
    }

    fun scrollToPreference(key: CharSequence) {
        preferenceFragmentDelegate.scrollToPreference(key.toString())
    }

    fun setDivider(divider: Drawable?) {
        preferenceFragmentDelegate.setDivider(divider)
    }

    fun setDividerHeight(height: Int) {
        preferenceFragmentDelegate.setDividerHeight(height)
    }
}