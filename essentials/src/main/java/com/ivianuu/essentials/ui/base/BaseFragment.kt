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
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.contributor.view.HasViewInjector
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.util.ViewInjectionContextWrapper
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.scopes.archlifecycle.fragment.viewOnDestroy
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.traveler.Router
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Base fragment
 */
abstract class BaseFragment : Fragment(), BackListener, HasSupportFragmentInjector,
    HasViewInjector, MvRxView, ViewModelFactoryHolder {

    @Inject lateinit var router: Router

    val coroutineScope = onDestroy.asMainCoroutineScope()

    val viewCoroutineScope
        get() = _viewCoroutineScope ?: throw IllegalStateException("view not attached")
    private var _viewCoroutineScope: CoroutineScope? = null

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    protected open val layoutRes = -1

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = if (layoutRes != -1) {
        val viewInjectionContext =
            ViewInjectionContextWrapper(requireContext(), this)
        val viewInjectionInflater = inflater.cloneInContext(viewInjectionContext)
        viewInjectionInflater.inflate(layoutRes, container, false)
    } else {
        super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewCoroutineScope = viewOnDestroy.asMainCoroutineScope()
    }

    override fun onStart() {
        super.onStart()
        postInvalidate()
    }

    override fun onDestroyView() {
        _viewCoroutineScope = null
        super.onDestroyView()
    }

    override fun invalidate() {
    }

    override fun handleBack(): Boolean {
        childFragmentManager.fragments.filterIsInstance<BackListener>().forEach {
            if (it.handleBack()) {
                return true
            }
        }

        return false
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun viewInjector(): AndroidInjector<View> = viewInjector

}