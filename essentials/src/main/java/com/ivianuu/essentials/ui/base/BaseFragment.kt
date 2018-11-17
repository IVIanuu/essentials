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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.util.HasInjectorsContextWrapper
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injectors.CompositeInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.fragment.inject
import com.ivianuu.scopes.archlifecycle.fragment.viewOnDestroy
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.traveler.Router
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Base fragment
 */
abstract class BaseFragment : Fragment(), HasInjectors, OnBackPressedCallback,
    MvRxView, ViewModelFactoryHolder {

    @Inject override lateinit var injectors: CompositeInjectors
    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject lateinit var router: Router

    val coroutineScope = onDestroy.asMainCoroutineScope()

    val viewCoroutineScope
        get() = _viewCoroutineScope ?: throw IllegalStateException("view not attached")
    private var _viewCoroutineScope: CoroutineScope? = null

    protected open val layoutRes get() = -1

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
        requireActivity().addOnBackPressedCallback(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = if (layoutRes != -1) {
        val injectorInflater = inflater.cloneInContext(
            HasInjectorsContextWrapper(requireContext(), this)
        )
        injectorInflater.inflate(layoutRes, container, false)
    } else {
        super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewCoroutineScope = viewOnDestroy.asMainCoroutineScope()
    }

    override fun onStart() {
        super.onStart()
        invalidate()
    }

    override fun onDestroyView() {
        _viewCoroutineScope = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().removeOnBackPressedCallback(this)
    }

    override fun invalidate() {
    }

    override fun handleOnBackPressed() = false

}