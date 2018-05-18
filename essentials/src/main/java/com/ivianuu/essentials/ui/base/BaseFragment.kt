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

package com.ivianuu.essentials.ui.base

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.autodispose.archcomponents.AndroidLifecycleScopeProvider
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.common.ViewLifecycleFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Base fragment
 */
abstract class BaseFragment : ViewLifecycleFragment(), BackListener, HasSupportFragmentInjector, Injectable {

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    protected open val layoutRes = -1

    val lifecycleScopeProvider: LifecycleScopeProvider<Lifecycle.Event> =
        AndroidLifecycleScopeProvider.from(this)

    val viewLifecycleScopeProvider: LifecycleScopeProvider<Lifecycle.Event> =
        AndroidLifecycleScopeProvider.from(viewLifecycleOwner)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes != -1) {
            inflater.inflate(layoutRes, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun handleBack(): Boolean {
        return false
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector
}