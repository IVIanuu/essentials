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

package com.ivianuu.essentials.ui.mvrx.director

import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.director.activity
import com.ivianuu.director.parentController
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.getViewModel
import com.ivianuu.essentials.ui.mvrx.viewModel
import com.ivianuu.injekt.get
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> EsController.activityViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = viewModel({ activity as ViewModelStoreOwner }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.parentViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = viewModel({ parentController as ViewModelStoreOwner }, key) { get<VM>() }

inline fun <reified VM : MvRxViewModel<*>> EsController.targetViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = viewModel({ targetController as ViewModelStoreOwner }, key) { get<VM>() }

inline fun <reified VM : MvRxViewModel<*>> EsController.getActivityViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getViewModel(activity as ViewModelStoreOwner, key) { get<VM>() }

inline fun <reified VM : MvRxViewModel<*>> EsController.getParentViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getViewModel(parentController as ViewModelStoreOwner, key) { get<VM>() }

inline fun <reified VM : MvRxViewModel<*>> EsController.getTargetViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getViewModel(targetController as ViewModelStoreOwner, key) { get<VM>() }