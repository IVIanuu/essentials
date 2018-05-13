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

import android.arch.lifecycle.ViewModelStore
import android.arch.lifecycle.ViewModelStoreOwner
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.autodispose.conductorfork.ControllerEvent
import com.ivianuu.autodispose.conductorfork.ControllerScopeProvider
import com.ivianuu.conductor.Controller
import com.ivianuu.essentials.injection.conductor.ConductorInjection
import com.ivianuu.essentials.injection.conductor.HasControllerInjector
import com.ivianuu.traveler.Router
import dagger.android.DispatchingAndroidInjector
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import javax.inject.Inject

/**
 * Base controller
 */
abstract class BaseController @JvmOverloads constructor(args: Bundle = Bundle()) : Controller(args),
    HasControllerInjector, LayoutContainer, LifecycleScopeProvider<ControllerEvent>, ViewModelStoreOwner {

    @Inject lateinit var controllerInjector: DispatchingAndroidInjector<Controller>

    @Inject lateinit var travelerRouter: Router

    override var containerView: View? = null

    protected open val layoutRes = -1

    private val lifecycleScopeProvider = ControllerScopeProvider.from(this)

    private val vmStore = ViewModelStore()

    private var injected = false

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        if (!injected) {
            injected = true
            ConductorInjection.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = if (layoutRes != -1) {
            inflater.inflate(layoutRes, container, false)
        } else {
            throw IllegalStateException("no layout res set")
        }

        containerView = view

        onViewCreated(view)

        return view
    }

    override fun onDestroyView(view: View) {
        clearFindViewByIdCache()
        containerView = null
        super.onDestroyView(view)
    }

    override fun lifecycle() = lifecycleScopeProvider.lifecycle()

    override fun correspondingEvents()=
        lifecycleScopeProvider.correspondingEvents()

    override fun peekLifecycle() = lifecycleScopeProvider.peekLifecycle()

    override fun controllerInjector() = controllerInjector

    override fun getViewModelStore(): ViewModelStore {
        return vmStore
    }

    protected open fun onViewCreated(view: View) {

    }
}