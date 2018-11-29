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
import com.ivianuu.director.Controller
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.util.ext.requireParentController
import com.ivianuu.essentials.util.ext.requireTargetController
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> BaseController.activityViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = activity.viewModelProvider(factory).get(key, VM::class.java).setupViewModel(this)

inline fun <reified VM : MvRxViewModel<*>> BaseController.bindActivityViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) = viewModelLazy { activityViewModel(keyProvider(), factory) }

inline fun <reified VM : MvRxViewModel<*>> BaseController.existingActivityViewModel(
    key: String = VM::class.defaultViewModelKey
) = activityViewModel<VM>(key, ExistingViewModelFactory())

inline fun <reified VM : MvRxViewModel<*>> BaseController.bindExistingActivityViewModel(
    key: String = VM::class.defaultViewModelKey
) = viewModelLazy { existingActivityViewModel<VM>(key) }

inline fun <reified VM : MvRxViewModel<*>> BaseController.parentViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = (requireParentController() as ViewModelStoreOwner).viewModelProvider(factory).get(
    key,
    VM::class.java
).setupViewModel(this)

inline fun <reified VM : MvRxViewModel<*>> BaseController.bindParentViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) = viewModelLazy { parentViewModel(keyProvider(), factory) }

inline fun <reified VM : MvRxViewModel<*>> BaseController.existingParentViewModel(
    key: String = VM::class.defaultViewModelKey
) = parentViewModel<VM>(key, ExistingViewModelFactory())

inline fun <reified VM : MvRxViewModel<*>> BaseController.bindExistingParentViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) = viewModelLazy { existingParentViewModel<VM>(keyProvider()) }

inline fun <reified VM : MvRxViewModel<*>> BaseController.targetViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = (requireTargetController() as ViewModelStoreOwner).viewModelProvider(factory)
    .get(key, VM::class.java).setupViewModel(this)

inline fun <T, reified VM : MvRxViewModel<*>> BaseController.bindTargetViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) where T : Controller =
    viewModelLazy { targetViewModel(keyProvider(), factory) }

inline fun <reified VM : MvRxViewModel<*>> BaseController.existingTargetViewModel(
    key: String = VM::class.defaultViewModelKey
) = targetViewModel<VM>(key, ExistingViewModelFactory())

inline fun <reified VM : MvRxViewModel<*>> BaseController.bindExistingTargetViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) = viewModelLazy { existingTargetViewModel<VM>(keyProvider()) }