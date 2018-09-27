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
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.director.Controller
import com.ivianuu.director.arch.lifecycle.ControllerLifecycleOwner
import com.ivianuu.director.arch.lifecycle.ControllerViewModelStoreOwner
import com.ivianuu.director.common.contextRef
import com.ivianuu.director.common.viewRef
import com.ivianuu.director.dialog.DialogController
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.director.DirectorInjection
import com.ivianuu.essentials.injection.director.HasControllerInjector
import com.ivianuu.essentials.injection.view.HasViewInjector
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.ViewInjectionContextWrapper
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.essentials.util.lifecycle.LifecycleCoroutineScope
import com.ivianuu.essentials.util.lifecycle.LifecycleJob
import com.ivianuu.essentials.util.lifecycle.LifecycleOwner2
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import com.ivianuu.rxlifecycle.RxLifecycleOwner
import com.ivianuu.traveler.Router
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base dialog controller
 */
abstract class BaseDialogController : DialogController(),
    CoroutineScope, Injectable, IdentifiableScreen,
    LifecycleOwner, LifecycleOwner2, RouterHolder, RxLifecycleOwner, ViewModelFactoryHolder,
    ViewModelStoreOwner {

    @set:Inject var travelerRouter: Router by contextRef()
    @set:Inject override var viewModelFactory: ViewModelProvider.Factory by contextRef()

    override val providedRouter: Router
        get() = travelerRouter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val lifecycleOwner = ControllerLifecycleOwner()
    private val viewModelStoreOwner = ControllerViewModelStoreOwner()

    val job by unsafeLazy { LifecycleJob(this) }

    override fun onContextAvailable(context: Context) {
        DirectorInjection.inject(this)
        super.onContextAvailable(context)
    }

    override fun getLifecycle() = lifecycleOwner.lifecycle

    override fun getViewModelStore() = viewModelStoreOwner.viewModelStore
}