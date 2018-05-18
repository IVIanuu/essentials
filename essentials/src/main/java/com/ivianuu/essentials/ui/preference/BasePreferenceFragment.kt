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

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceScreen
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.autodispose.archcomponents.AndroidLifecycleScopeProvider
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.common.ViewLifecyclePreferenceFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Base preference fragment
 */
abstract class BasePreferenceFragment : ViewLifecyclePreferenceFragment(), Injectable, BackListener,
    HasSupportFragmentInjector {

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    open val layoutRes = -1
    open val prefsContainerId = -1
    open val prefsRes = -1

    val lifecycleScopeProvider: LifecycleScopeProvider<Lifecycle.Event> =
        AndroidLifecycleScopeProvider.from(this)

    val viewLifecycleScopeProvider: LifecycleScopeProvider<Lifecycle.Event> =
        AndroidLifecycleScopeProvider.from(viewLifecycleOwner)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes != -1 && prefsContainerId != -1) {
            val view = inflater.inflate(layoutRes, container, false)
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

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return EnabledAwarePreferenceAdapter(preferenceScreen)
    }

    override fun handleBack(): Boolean {
        return false
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

}
