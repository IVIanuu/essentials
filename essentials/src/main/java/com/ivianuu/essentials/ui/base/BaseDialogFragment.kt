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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.traveler.Router
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Base dialog fragment
 */
abstract class BaseDialogFragment : AppCompatDialogFragment(), OnBackPressedCallback,
    ViewModelFactoryHolder {

    @Inject lateinit var router: Router
    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    val coroutineScope = onDestroy.asMainCoroutineScope()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        requireActivity().addOnBackPressedCallback(this)
    }

    override fun onDetach() {
        requireActivity().removeOnBackPressedCallback(this)
        super.onDetach()
    }

    override fun handleOnBackPressed() = false

}