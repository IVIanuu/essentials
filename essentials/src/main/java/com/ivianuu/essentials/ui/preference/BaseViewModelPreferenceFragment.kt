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

package com.ivianuu.essentials.ui.preference

import android.arch.lifecycle.ViewModel
import com.ivianuu.essentials.util.DaggerViewModelFactory
import com.ivianuu.essentials.util.ext.getViewModel
import com.ivianuu.essentials.util.ext.unsafeLazy
import javax.inject.Inject

/**
 * Essentials view model preference fragment
 */
abstract class BaseViewModelPreferenceFragment<VM : ViewModel> : BasePreferenceFragment() {
    @Inject lateinit var viewModelFactory: DaggerViewModelFactory<VM>
    protected val viewModel: VM by unsafeLazy { getViewModel<ViewModel>(viewModelFactory) as VM }
}