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
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.key.keyModule
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.InjektTraitContextWrapper
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.fragmentComponent
import com.ivianuu.injekt.inject
import com.ivianuu.traveler.Router

/**
 * Base fragment
 */
abstract class EsFragment : Fragment(), ContextAware, InjektTrait, MvRxView {

    override val component by unsafeLazy {
        fragmentComponent(
            modules = listOf(keyModule(arguments)) + modules()
        )
    }

    override val providedContext: Context
        get() = requireContext()

    val router by inject<Router>()

    protected open val layoutRes get() = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        check(layoutRes != -1) { "no layoutRes provided" }
        val injectorInflater =
            inflater.cloneInContext(InjektTraitContextWrapper(requireContext(), this))
        return injectorInflater.inflate(layoutRes, container, false)
    }

    override fun onStart() {
        super.onStart()
        invalidate()
    }

    override fun invalidate() {
    }

    protected open fun modules(): List<Module> = emptyList()

}