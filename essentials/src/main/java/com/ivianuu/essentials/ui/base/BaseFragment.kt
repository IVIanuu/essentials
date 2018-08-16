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
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.KtHasSupportFragmentInjector
import com.ivianuu.essentials.ui.common.back.BackListener
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.traveler.Router
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * Base fragment
 */
abstract class BaseFragment : Fragment(), BackListener, KtHasSupportFragmentInjector,
    Injectable, IdentifiableScreen, ContextAware, RouterHolder {

    @Inject override lateinit var router: Router

    @Inject override lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    val scopeProvider = AndroidLifecycleScopeProvider.from(this)
    lateinit var viewScopeProvider: LifecycleScopeProvider<Lifecycle.Event>

    protected open val layoutRes = -1

    override val providedContext: Context
        get() = requireActivity()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = if (layoutRes != -1) {
        inflater.inflate(layoutRes, container, false)
    } else {
        super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewScopeProvider = AndroidLifecycleScopeProvider.from(viewLifecycleOwner)
    }
}