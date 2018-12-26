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
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> EsController.activityViewModel(
    crossinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) = viewModel({ activity }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.existingActivityViewModel(
    crossinline key: () -> String = { VM::class.defaultViewModelKey }
) = existingViewModel<VM>({ activity }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.parentViewModel(
    crossinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) = viewModel({ parentController as ViewModelStoreOwner }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.existingParentViewModel(
    crossinline key: () -> String = { VM::class.defaultViewModelKey }
) = existingViewModel<VM>({ parentController as ViewModelStoreOwner }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.targetViewModel(
    crossinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) = viewModel({ targetController as ViewModelStoreOwner }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.existingTargetViewModel(
    crossinline key: () -> String = { VM::class.defaultViewModelKey }
) = existingViewModel<VM>({ targetController as ViewModelStoreOwner }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getActivityViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = getViewModel(activity, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingActivityViewModel(
    key: String = VM::class.defaultViewModelKey
) = getExistingViewModel<VM>(activity, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getParentViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = getViewModel(parentController as ViewModelStoreOwner, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingParentViewModel(
    key: String = VM::class.defaultViewModelKey
) = getExistingViewModel<VM>(parentController as ViewModelStoreOwner, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getTargetViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = getViewModel(targetController as ViewModelStoreOwner, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingTargetViewModel(
    key: String = VM::class.defaultViewModelKey
) = getExistingViewModel<VM>(targetController as ViewModelStoreOwner, key)
