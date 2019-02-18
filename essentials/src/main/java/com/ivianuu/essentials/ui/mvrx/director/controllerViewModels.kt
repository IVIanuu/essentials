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

import com.ivianuu.director.activity
import com.ivianuu.director.parentController
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.getViewModel
import com.ivianuu.essentials.ui.mvrx.viewModel
import com.ivianuu.kommon.lifecycle.defaultViewModelKey
import com.ivianuu.stdlibx.cast

inline fun <reified VM : MvRxViewModel<*>> EsController.activityViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = viewModel({ activity.cast() }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.parentViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = viewModel({ parentController.cast() }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.targetViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = viewModel({ targetController.cast() }, key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getActivityViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = getViewModel(activity.cast(), key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getParentViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = getViewModel(parentController.cast(), key, factory)

inline fun <reified VM : MvRxViewModel<*>> EsController.getTargetViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = getViewModel(targetController.cast(), key, factory)