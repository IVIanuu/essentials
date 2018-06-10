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

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceScreen
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.KtHasSupportFragmentInjector
import com.ivianuu.essentials.injection.KtHasViewInjector
import com.ivianuu.essentials.ui.common.back.BackListener
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.ViewInjectionContextWrapper
import com.ivianuu.essentials.util.screenlogger.NamedScreen
import com.ivianuu.traveler.Router
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * Base preference fragment
 */
abstract class BasePreferenceFragment : PreferenceFragmentCompat(),
    BackListener, KtHasSupportFragmentInjector,
    KtHasViewInjector, Injectable, NamedScreen,
    ContextAware {

    @Inject lateinit var router: Router

    @Inject override lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject override lateinit var viewInjector: DispatchingAndroidInjector<View>

    open val layoutRes = -1
    open val prefsContainerId = -1
    open val prefsRes = -1

    override val providedContext: Context
        get() = requireActivity()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes != -1 && prefsContainerId != -1) {
            val viewInjectionContext =
                ViewInjectionContextWrapper(requireContext(), this)
            val viewInjectionInflater = inflater.cloneInContext(viewInjectionContext)
            viewInjectionInflater.inflate(layoutRes, container, false)
            val view = viewInjectionInflater.inflate(layoutRes, container, false)
            val prefsContainer = view.findViewById<ViewGroup>(prefsContainerId)
            val prefsView = super.onCreateView(inflater, prefsContainer, savedInstanceState)
            prefsContainer.addView(prefsView)
            view
        } else {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        if (prefsRes != -1) {
            addPreferencesFromResource(prefsRes)
        }
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> =
        EnabledAwarePreferenceAdapter(preferenceScreen)
}