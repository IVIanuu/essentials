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

package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.injekt.get
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> EsController.activityViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = viewModel({ activity as ViewModelStoreOwner }, key) { get<VM>() }

inline fun <reified VM : MvRxViewModel<*>> EsController.existingActivityViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = existingViewModel({ activity as ViewModelStoreOwner }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.parentViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = viewModel({ parentController as ViewModelStoreOwner }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.existingParentViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = existingViewModel({ parentController as ViewModelStoreOwner }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.targetViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = viewModel({ targetController as ViewModelStoreOwner }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.existingTargetViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = existingViewModel({ targetController as ViewModelStoreOwner }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getActivityViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = getViewModel(activity as ViewModelStoreOwner, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingActivityViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getExistingViewModel(activity as ViewModelStoreOwner, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getParentViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = getViewModel(parentController as ViewModelStoreOwner, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingParentViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getExistingViewModel(parentController as ViewModelStoreOwner, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getTargetViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = getViewModel(targetController as ViewModelStoreOwner, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingTargetViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getExistingViewModel(targetController as ViewModelStoreOwner, key)