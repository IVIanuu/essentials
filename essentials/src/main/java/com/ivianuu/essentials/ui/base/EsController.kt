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
import androidx.lifecycle.Lifecycle
import com.ivianuu.director.Controller
import com.ivianuu.director.activity
import com.ivianuu.director.androidx.lifecycle.lifecycleOwner
import com.ivianuu.director.context
import com.ivianuu.essentials.injection.controllerComponent
import com.ivianuu.essentials.ui.mvrx.injekt.InjektMvRxView
import com.ivianuu.essentials.ui.traveler.key.keyModule
import com.ivianuu.essentials.ui.viewmodel.ViewModelManager
import com.ivianuu.essentials.ui.viewmodel.director.viewModelManagerOwner
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.InjektTraitContextWrapper
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.modules
import com.ivianuu.traveler.Router
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*

/**
 * Base controller
 */
abstract class EsController : Controller(), ContextAware, InjektMvRxView, LayoutContainer {

    override val component by unsafeLazy {
        controllerComponent {
            modules(keyModule(args))
            modules(this@EsController.modules())
        }
    }

    override val containerView: View?
        get() = _containerView
    protected var _containerView: View? = null

    override val providedContext: Context
        get() = context

    val travelerRouter by inject<Router>()

    protected open val layoutRes get() = -1

    override val viewModelManager: ViewModelManager
        get() = viewModelManagerOwner.viewModelManager

    init {
        viewModelManagerOwner // todo remove this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        check(layoutRes != -1) { "no layoutRes provided" }
        val injectorInflater =
            inflater.cloneInContext(InjektTraitContextWrapper(activity, this))
        return injectorInflater.inflate(layoutRes, container, false)
            .also { setContentView(it, savedViewState) }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        invalidate()
    }

    override fun onDestroyView(view: View) {
        clearFindViewByIdCache()
        _containerView = null
        super.onDestroyView(view)
    }

    override fun invalidate() {
    }

    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle

    protected open fun modules(): List<Module> = emptyList()

    protected fun setContentView(view: View, savedViewState: Bundle?) {
        _containerView = view
        onViewCreated(view, savedViewState)
    }

    protected open fun onViewCreated(view: View, savedViewState: Bundle?) {
    }

}