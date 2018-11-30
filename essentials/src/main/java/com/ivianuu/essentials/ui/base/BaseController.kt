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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.arch.lifecycle.LifecycleController
import com.ivianuu.director.scopes.unbindView
import com.ivianuu.essentials.injection.inject
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.HasInjectorsContextWrapper
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injectors.CompositeInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.traveler.Router
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Base controller
 */
abstract class BaseController : LifecycleController(), ContextAware, HasInjectors,
    LayoutContainer, MvRxView {

    @Inject override lateinit var injectors: CompositeInjectors

    @Inject lateinit var travelerRouter: Router

    override val containerView: View?
        get() = view

    override val providedContext: Context
        get() = activity

    val coroutineScope = onDestroy.asMainCoroutineScope()

    val viewCoroutineScope
        get() = _viewCoroutineScope ?: throw IllegalStateException("view not attached")
    private var _viewCoroutineScope: CoroutineScope? = null

    protected open val layoutRes get() = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View = if (layoutRes != -1) {
        val injectorInflater =
            inflater.cloneInContext(HasInjectorsContextWrapper(activity, this))
        injectorInflater.inflate(layoutRes, container, false)
    } else {
        throw IllegalStateException("no layoutRes provided")
    }

    override fun onBindView(view: View, savedViewState: Bundle?) {
        super.onBindView(view, savedViewState)
        _viewCoroutineScope = unbindView.asMainCoroutineScope()
    }

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        super.onRestoreViewState(view, savedViewState)
        invalidate()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        invalidate()
    }

    override fun onUnbindView(view: View) {
        _viewCoroutineScope = null
        clearFindViewByIdCache()
        super.onUnbindView(view)
    }

    override fun invalidate() {
    }

}