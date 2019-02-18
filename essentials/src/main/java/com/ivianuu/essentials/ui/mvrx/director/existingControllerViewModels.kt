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
import com.ivianuu.essentials.ui.mvrx.existingViewModel
import com.ivianuu.essentials.ui.mvrx.getExistingViewModel
import com.ivianuu.kommon.lifecycle.defaultViewModelKey
import com.ivianuu.stdlibx.cast

inline fun <reified VM : MvRxViewModel<*>> EsController.existingActivityViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = existingViewModel({ activity.cast() }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.existingParentViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = existingViewModel({ parentController.cast() }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.existingTargetViewModel(
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = existingViewModel({ targetController.cast() }, key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingActivityViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getExistingViewModel(activity.cast(), key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingParentViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getExistingViewModel(parentController.cast(), key)

inline fun <reified VM : MvRxViewModel<*>> EsController.getExistingTargetViewModel(
    key: String = VM::class.defaultViewModelKey
): VM = getExistingViewModel(targetController.cast(), key)